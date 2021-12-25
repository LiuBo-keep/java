package org.springframework.cache;

import java.util.concurrent.Callable;
import org.springframework.lang.Nullable;

// 缓存接口 主要定义了一些操作缓存的常用操作
public interface Cache {
    String getName();

    Object getNativeCache();

    @Nullable
    Cache.ValueWrapper get(Object var1);

    @Nullable
    <T> T get(Object var1, @Nullable Class<T> var2);

    @Nullable
    <T> T get(Object var1, Callable<T> var2);

    void put(Object var1, @Nullable Object var2);

    @Nullable
    default Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Cache.ValueWrapper existingValue = this.get(key);
        if (existingValue == null) {
            this.put(key, value);
        }

        return existingValue;
    }

    void evict(Object var1);

    default boolean evictIfPresent(Object key) {
        this.evict(key);
        return false;
    }

    void clear();

    default boolean invalidate() {
        this.clear();
        return false;
    }

    public static class ValueRetrievalException extends RuntimeException {
        @Nullable
        private final Object key;

        public ValueRetrievalException(@Nullable Object key, Callable<?> loader, Throwable ex) {
            super(String.format("Value for key '%s' could not be loaded using '%s'", key, loader), ex);
            this.key = key;
        }

        @Nullable
        public Object getKey() {
            return this.key;
        }
    }

    @FunctionalInterface
    public interface ValueWrapper {
        @Nullable
        Object get();
    }
}
