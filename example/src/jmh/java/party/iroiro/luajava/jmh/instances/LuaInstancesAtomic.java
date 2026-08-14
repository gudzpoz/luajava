package party.iroiro.luajava.jmh.instances;

import org.jspecify.annotations.Nullable;
import party.iroiro.luajava.LuaInstances;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class LuaInstancesAtomic<T> extends LuaInstances<T> {
    private volatile AtomicReferenceArray<@Nullable Object> instances;
    private volatile int usedSlots;
    private volatile int freeEntries;
    private int lastFreeId;

    LuaInstancesAtomic() {
        instances = new AtomicReferenceArray<>(8);
        usedSlots = 0;
        freeEntries = 0;
        lastFreeId = -1;
    }

    protected synchronized int addNullable(@Nullable T instance) {
        AtomicReferenceArray<@Nullable Object> instances = this.instances;
        int id = lastFreeId;

        if (id == -1) {
            id = usedSlots++;
            int slots = instances.length();
            if (id < slots) {
                instances.set(id, instance);
            } else {
                AtomicReferenceArray<@Nullable Object> newInstances = new AtomicReferenceArray<>(slots + (slots >> 1));
                for (int i = 0; i < slots; i++) {
                    newInstances.set(i, instances.get(i));
                }
                instances = newInstances;
                instances.set(id, instance);
                this.instances = instances;
            }
        } else {
            lastFreeId = (Integer) Objects.requireNonNull(instances.get(id));
            instances.set(id, instance);
            freeEntries--;
        }
        return id;
    }

    protected LuaInstances.Token<T> add() {
        int id = addNullable(null);
        //noinspection Convert2Lambda
        return new LuaInstances.Token<>(id, new LuaInstances.Token.Consumer<T>() {
            @Override
            public void accept(T lua) {
                LuaInstancesAtomic.this.set(id, lua);
            }
        });
    }

    private synchronized void set(int id, @Nullable T instance) {
        instances.set(id, instance);
    }

    @SuppressWarnings("unchecked")
    protected T get(int id) {
        return (T) Objects.requireNonNull(instances.get(id));
    }

    protected synchronized void remove(int id) {
        int lastId = usedSlots - 1;
        if (id == lastId) {
            instances.set(id, null);
            usedSlots = lastId;
        } else {
            instances.set(id, lastFreeId);
            lastFreeId = id;
            freeEntries++;
        }
    }

    protected int size() {
        return usedSlots - freeEntries;
    }
}
