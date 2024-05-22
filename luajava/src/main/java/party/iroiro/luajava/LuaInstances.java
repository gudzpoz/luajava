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
import java.util.HashSet;
import java.util.Iterator;

/**
 * A collection of {@link Jua} instances, each labeled with a unique id
 */
public class LuaInstances<T> {
    private final ArrayList<T> instances;
    private final HashSet<Integer> freeIds;

    protected LuaInstances() {
        freeIds = new HashSet<>();
        instances = new ArrayList<>();
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
    protected synchronized T get(int id) {
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
    protected synchronized void remove(int id) {
        if (id == instances.size() - 1) {
            instances.remove(id);
        } else {
            instances.set(id, null);
            freeIds.add(id);
        }
    }

    /**
     * @return the number of elements in this collection
     */
    protected synchronized int size() {
        return instances.size() - freeIds.size();
    }

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
