/*
 * Copyright (C) 2022 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package party.iroiro.luajava;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * A collection of {@link Jua} instances, each labeled with a unique id
 *
 * @param <T> instance type
 */
public class LuaInstances<T> {
    private static final int SEGMENT_SHIFT = 12;
    private static final int SEGMENT_MASK = (1 << SEGMENT_SHIFT) - 1;

    private final AtomicReferenceArray<@Nullable AtomicReferenceArray<@Nullable T>> segments;
    private final AtomicInteger nextId;
    private final AtomicReferenceArray<@Nullable Node> freeHeads;

    /// Creates an empty collection
    protected LuaInstances() {
        segments = new AtomicReferenceArray<>(1 << SEGMENT_SHIFT);
        nextId = new AtomicInteger(0);
        freeHeads = new AtomicReferenceArray<>(Math.min(16, Runtime.getRuntime().availableProcessors()));
    }

    protected int add(T instance) {
        return addNullable(instance);
    }

    /**
     * Adds the instance to the collection and allocates a new id
     *
     * @param instance element to be added to this collection
     * @return the allocated id
     */
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
                LuaInstances.this.set(id, lua);
            }
        });
    }

    private void set(int id, @Nullable T instance) {
        int segment = id >>> SEGMENT_SHIFT;
        int offset = id & SEGMENT_MASK;
        Objects.requireNonNull(segments.get(segment)).set(offset, instance);
    }

    /**
     * Returns the instance with the specified id
     * @param id id of the instance to return
     * @return the element with the specified id
     */
    protected T get(int id) {
        int segment = id >>> SEGMENT_SHIFT;
        int offset = id & SEGMENT_MASK;
        return Objects.requireNonNull(
                Objects.requireNonNull(segments.get(segment)).get(offset)
        );
    }

    /**
     * Removes the instance from the collection, marking the id as usable again
     *
     * <p>
     * The user is responsible for {@link AutoCloseable} resources.
     * </p>
     *
     * @param id the id of the instance to be removed
     */
    protected void remove(int id) {
        set(id, null);
        pushFreeId(id);
    }

    /**
     * Returns the number of elements in this collection
     *
     * <p>
     * The returned number is only accurate when no concurrent modifications are being made.
     * </p>
     *
     * @return the number of elements in this collection
     */
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

    /// A place in the instance list
    public static class Token<T> {
        /**
         * Replacing the Consumer interface for lower versions
         * <p>
         * {@code Call requires API level 24 (current min is 19): java.util.function.Consumer#accept}
         * @param <T> type
         */
        public interface Consumer<T> {
            void accept(T t);
        }
        public final int id;
        public final Consumer<T> setter;

        public Token(int id, Consumer<T> setter) {
            this.id = id;
            this.setter = setter;
        }
    }
}
