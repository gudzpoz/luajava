package party.iroiro.jua;

import com.google.errorprone.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A collection of {@link Jua} instances, each labeled with a unique id
 */
class JuaInstances {
    private final ArrayList<Jua> instances;
    private final HashSet<Integer> freeIds;

    JuaInstances() {
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
    synchronized int add(@NotNull Jua instance) {
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

    /**
     * Returns the instance with the specified id
     * @param id id of the instance to return
     * @return the element with the specified id
     */
    @CheckReturnValue
    synchronized Jua get(int id) {
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
}
