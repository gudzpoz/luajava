package party.iroiro.luajava.util;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A two-level roughly-LRU-cache based on {@link ConcurrentHashMap}
 *
 * <p>
 * Basically, this class is intended for method cache with usage like
 * {@code LRUCache<Class<?>, String, Method>}.
 * </p>
 *
 * @param <K1> first level key
 * @param <K2> second level key
 * @param <V> value
 */
public final class LRUCache<K1, K2, V> {

    private final Cache<Key<K1, K2>, V> cache;

    public LRUCache(int maxSize) {
        this.cache = new Cache<>(maxSize);
    }

    @Nullable
    public V get(K1 k1, K2 k2) {
        return cache.get(new Key<>(k1, k2));
    }

    public void put(K1 k1, K2 k2, V v) {
        cache.put(new Key<>(k1, k2), v);
    }

    public static final class Cache<K, V> {
        private final int maxSize;
        private final ConcurrentHashMap<K, Node<V>> cache = new ConcurrentHashMap<>();
        private final AtomicBoolean evictLock = new AtomicBoolean(false);

        public Cache(int maxSize) {
            this.maxSize = maxSize;
        }

        @Nullable
        public V get(K k) {
            Node<V> node = cache.get(k);
            if (node == null) {
                return null;
            }
            if (!node.accessed) {
                node.accessed = true;
            }
            return node.value;
        }

        public void put(K k, V v) {
            cache.put(k, new Node<>(v));

            int n = cache.size() - maxSize;
            if (n > 0) {
                tryEvict(n);
            }
        }

        private void tryEvict(int n) {
            if (!evictLock.compareAndSet(false, true)) {
                return;
            }
            try {
                for (Map.Entry<K, Node<V>> entry : cache.entrySet()) {
                    Node<V> node = entry.getValue();
                    if (node.accessed) {
                        node.accessed = false;
                    } else {
                        cache.remove(entry.getKey());
                        n--;
                        if (n <= 0) {
                            break;
                        }
                    }
                }
            } finally {
                evictLock.set(false);
            }
        }
    }

    private static final class Key<K1, K2> {
        final K1 k1;
        final K2 k2;

        Key(K1 k1, K2 k2) {
            this.k1 = k1;
            this.k2 = k2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key<?, ?> key = (Key<?, ?>) o;
            return Objects.equals(k1, key.k1) && Objects.equals(k2, key.k2);
        }

        @Override
        public int hashCode() {
            return 31 * k1.hashCode() + k2.hashCode();
        }
    }

    private static final class Node<V> {
        final V value;
        volatile boolean accessed = true;

        Node(V value) {
            this.value = value;
        }
    }

}
