package party.iroiro.luajava.util;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * An LRU-cache based on {@link LinkedHashMap}
 *
 * <p>
 * Basically, this class is intended for method cache with usage like
 * {@code LRUCache<Class<?>, String, Method>}.
 * </p>
 */
public final class LRUCache<K1, K2, V> {

    private final int innerSize;
    private final List<Map<K1, Map<K2, V>>> cacheShards;

    public LRUCache(int level1Size, int level2Size, int shards) {
        this.innerSize = level2Size;
        ArrayList<Map<K1, Map<K2, V>>> shardList = new ArrayList<>(shards);
        for (int i = 0; i < shards; i++) {
            shardList.add(Collections.synchronizedMap(new Cache<>(level1Size)));
        }
        this.cacheShards = Collections.unmodifiableList(shardList);
    }

    @Nullable
    public V get(K1 k1, K2 k2) {
        Map<K2, V> inner = getInnerCache(k1);
        return inner.get(k2);
    }

    private Map<K2, V> getInnerCache(K1 k1) {
        int shard = k1.hashCode() % cacheShards.size();
        Map<K1, Map<K2, V>> cache = cacheShards.get(shard);
        Map<K2, V> inner = cache.get(k1);
        if (inner == null) {
            inner = Collections.synchronizedMap(new Cache<>(innerSize));
            Map<K2, V> prev = cache.putIfAbsent(k1, inner);
            if (prev != null) {
                inner = prev;
            }
        }
        return inner;
    }

    public void put(K1 k1, K2 k2, V v) {
        Map<K2, V> inner = getInnerCache(k1);
        inner.putIfAbsent(k2, v);
    }

    private final static class Cache<K, V> extends LinkedHashMap<K, V> {
        private final int maxEntries;

        private Cache(int maxEntries) {
            super(maxEntries + 1, 0.75F, true);
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxEntries;
        }
    }

}
