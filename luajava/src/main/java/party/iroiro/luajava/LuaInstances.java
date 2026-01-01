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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * A collection of {@link Jua} instances, each labeled with a unique id
 *
 * @param <T> instance type
 */
public class LuaInstances<T> {
    private final ArrayList<Object> instances;
    private int freeEntries;
    private int lastFreeId;

    /// Creates an empty collection
    protected LuaInstances() {
        instances = new ArrayList<>();
        freeEntries = 0;
        lastFreeId = -1;
    }

    /**
     * Adds the instance to the collection and allocates a new id
     *
     * @param instance element to be added to this collection
     * @return the allocated id
     */
    protected synchronized int add(@NotNull T instance) {
        return addNullable(instance);
    }

    protected synchronized int addNullable(@Nullable T instance) {
        int id;
        if (lastFreeId == -1) {
            id = instances.size();
            instances.add(instance);
        } else {
            id = lastFreeId;
            lastFreeId = (Integer) instances.get(lastFreeId);
            instances.set(id, instance);
            freeEntries--;
        }
        return id;
    }

    protected synchronized Token<T> add() {
        int id = addNullable(null);
        //noinspection Convert2Lambda
        return new Token<>(id, new Token.Consumer<T>() {
            @Override
            public void accept(T lua) {
                LuaInstances.this.set(id, lua);
            }
        });
    }

    private synchronized void set(int id, @Nullable T instance) {
        instances.set(id, instance);
    }

    /**
     * Returns the instance with the specified id
     * @param id id of the instance to return
     * @return the element with the specified id
     */
    @SuppressWarnings("unchecked")
    protected synchronized T get(int id) {
        return (T) instances.get(id);
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
    protected synchronized void remove(int id) {
        if (id == instances.size() - 1) {
            instances.remove(id);
        } else {
            instances.set(id, lastFreeId);
            lastFreeId = id;
            freeEntries++;
        }
    }

    /**
     * Returns the number of elements in this collection
     *
     * @return the number of elements in this collection
     */
    protected synchronized int size() {
        return instances.size() - freeEntries;
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

        private Token(int id, Consumer<T> setter) {
            this.id = id;
            this.setter = setter;
        }
    }
}
