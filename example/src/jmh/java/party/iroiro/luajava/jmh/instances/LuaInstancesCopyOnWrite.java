package party.iroiro.luajava.jmh.instances;

import org.jspecify.annotations.Nullable;
import party.iroiro.luajava.LuaInstances;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LuaInstancesCopyOnWrite<T> extends LuaInstances<T> {
    private final CopyOnWriteArrayList<@Nullable Object> instances;
    private volatile int freeEntries;
    private int lastFreeId;

    LuaInstancesCopyOnWrite() {
        instances = new CopyOnWriteArrayList<>();
        freeEntries = 0;
        lastFreeId = -1;
    }

    protected synchronized int addNullable(@Nullable T instance) {
        CopyOnWriteArrayList<@Nullable Object> instances = this.instances;
        int id = lastFreeId;

        if (id == -1) {
            id = instances.size();
            instances.add(instance);
        } else {
            lastFreeId = (Integer) Objects.requireNonNull(this.instances.get(id));
            this.instances.set(id, instance);
            freeEntries--;
        }
        return id;
    }

    protected Token<T> add() {
        int id = addNullable(null);
        //noinspection Convert2Lambda
        return new LuaInstances.Token<>(id, new Token.Consumer<T>() {
            @Override
            public void accept(T lua) {
                LuaInstancesCopyOnWrite.this.set(id, lua);
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
        int lastId = this.instances.size() - 1;
        if (id == lastId) {
            instances.remove(lastId);
        } else {
            instances.set(id, lastFreeId);
            lastFreeId = id;
            freeEntries++;
        }
    }

    protected int size() {
        return this.instances.size() - freeEntries;
    }
}
