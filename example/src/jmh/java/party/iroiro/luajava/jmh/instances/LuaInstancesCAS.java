package party.iroiro.luajava.jmh.instances;

import org.jspecify.annotations.Nullable;
import party.iroiro.luajava.LuaInstances;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class LuaInstancesCAS<T> extends LuaInstances<T> {
    private static final int SEGMENT_SHIFT = 12;
    private static final int SEGMENT_MASK = (1 << SEGMENT_SHIFT) - 1;

    private final AtomicReferenceArray<@Nullable AtomicReferenceArray<@Nullable T>> segments;
    private final AtomicInteger nextId;
    private final AtomicReferenceArray<@Nullable Node> freeHeads;

    LuaInstancesCAS() {
        segments = new AtomicReferenceArray<>(1 << SEGMENT_SHIFT);
        nextId = new AtomicInteger(0);
        freeHeads = new AtomicReferenceArray<>(Runtime.getRuntime().availableProcessors());
    }

    protected int addNullable(@Nullable T instance) {
        int id = popFreeId();
        AtomicReferenceArray<@Nullable T> segment;
        if (id == -1) {
            id = nextId.getAndIncrement();
            int segmentIndex = id >> SEGMENT_SHIFT;
            segment = segments.get(segmentIndex);
            if (segment == null) {
                segment = new AtomicReferenceArray<>(1 << SEGMENT_SHIFT);
                if (!segments.compareAndSet(segmentIndex, null, segment)) {
                    segment = Objects.requireNonNull(segments.get(segmentIndex));
                }
            }
        } else {
            segment = Objects.requireNonNull(segments.get(id >>> SEGMENT_SHIFT));
        }
        segment.set(id & SEGMENT_MASK, instance);
        return id;
    }

    protected Token<T> add() {
        int id = addNullable(null);
        //noinspection Convert2Lambda
        return new Token<>(id, new Token.Consumer<T>() {
            @Override
            public void accept(T lua) {
                LuaInstancesCAS.this.set(id, lua);
            }
        });
    }

    private void set(int id, @Nullable T instance) {
        int segment = id >>> SEGMENT_SHIFT;
        int offset = id & SEGMENT_MASK;
        Objects.requireNonNull(segments.get(segment)).set(offset, instance);
    }

    protected T get(int id) {
        int segment = id >>> SEGMENT_SHIFT;
        int offset = id & SEGMENT_MASK;
        return Objects.requireNonNull(
                Objects.requireNonNull(segments.get(segment)).get(offset)
        );
    }

    protected void remove(int id) {
        set(id, null);
        pushFreeId(id);
    }

    protected int size() {
        int freeCount = 0;
        for (int i = 0; i < freeHeads.length(); i++) {
            Node head = freeHeads.get(i);
            while (head != null) {
                head = head.next;
                freeCount++;
            }
        }
        return nextId.get() - freeCount;
    }

    private void pushFreeId(int id) {
        int n = Thread.currentThread().hashCode() % freeHeads.length();
        Node oldHead, newHead;
        do {
            oldHead = freeHeads.get(n);
            newHead = new Node(id, oldHead);
        } while (!freeHeads.compareAndSet(n, oldHead, newHead));
    }

    private int popFreeId() {
        int n = Thread.currentThread().hashCode() % freeHeads.length();
        Node oldHead, newHead;
        do {
            oldHead = freeHeads.get(n);
            if (oldHead == null) {
                return -1;
            }
            newHead = oldHead.next;
        } while (!freeHeads.compareAndSet(n, oldHead, newHead));
        return oldHead.id;
    }

    private static class Node {
        final int id;
        @Nullable
        final Node next;

        Node(int id, @Nullable Node next) {
            this.id = id;
            this.next = next;
        }
    }
}
