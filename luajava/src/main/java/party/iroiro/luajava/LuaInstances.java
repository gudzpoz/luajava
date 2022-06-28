package party.iroiro.luajava;

import com.google.errorprone.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A collection of {@link Jua} instances, each labeled with a unique id
 */
class LuaInstances<T extends Lua> {
    private final ArrayList<T> instances;
    private final HashSet<Integer> freeIds;

    LuaInstances() {
        freeIds = new HashSet<>();
        instances = new ArrayList<>();
    }

    /**
     * Adds the instance to the collection and allocates a new id
     *
     * @param instance element to be added to this collection
     * @return the allocated id
     */
    @CheckReturnValue
    synchronized int add(@NotNull T instance) {
        return addNullable(instance);
    }

    private synchronized int addNullable(@Nullable T instance) {
        if (freeIds.isEmpty()) {
            int id = instances.size();
            instances.add(instance);
            return id;
        } else {
            Iterator<Integer> first = freeIds.iterator();
            Integer id = first.next();
            first.remove();
            instances.set(id, instance);
            return id;
        }
    }

    @CheckReturnValue
    synchronized Token<T> add() {
        int id = addNullable(null);
        return new Token<>(id, lua -> instances.set(id, lua));
    }

    /**
     * Returns the instance with the specified id
     * @param id id of the instance to return
     * @return the element with the specified id
     */
    @CheckReturnValue
    synchronized T get(int id) {
        return instances.get(id);
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
    synchronized void remove(int id) {
        if (id == instances.size() - 1) {
            //noinspection resource
            instances.remove(id);
        } else {
            //noinspection resource
            instances.set(id, null);
            freeIds.add(id);
        }
    }

    /**
     * @return the number of elements in this collection
     */
    synchronized int size() {
        return instances.size() - freeIds.size();
    }

    public static class Token<T> {
        public final int id;
        public final Consumer<T> setter;

        private Token(int id, Consumer<T> setter) {
            this.id = id;
            this.setter = setter;
        }
    }
}
