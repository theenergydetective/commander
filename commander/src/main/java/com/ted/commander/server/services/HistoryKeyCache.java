package com.ted.commander.server.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.history.HistoryKey;
import com.ted.commander.server.dao.CacheRemovalListener;

import java.util.concurrent.TimeUnit;

public class HistoryKeyCache<T> {

    Cache<Long, Cache<Long, T>> virtualECCCache;
    final int size;
    final int duration;
    final TimeUnit timeUnit;

    public HistoryKeyCache(int size, int duration, TimeUnit timeUnit){
        this.size =size;
        this.duration = duration;
        this.timeUnit = timeUnit;
        virtualECCCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.DAYS).maximumSize(5000).initialCapacity(2000).removalListener(new CacheRemovalListener()).build();
    }

    private Cache<Long, T> getHistoryCache(Long virtualECCId){
        Cache<Long, T> cache = virtualECCCache.getIfPresent(virtualECCId);
        if (cache == null){
            cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(duration, timeUnit).maximumSize(size).initialCapacity(size).build();
            virtualECCCache.put(virtualECCId, cache);
        }
        return cache;
    }

    public void invalidate(HistoryKey key){
        getHistoryCache(key.getVirtualECCId()).invalidate(key.getStartEpoch());
    }

    public T get(HistoryKey key){
        return getHistoryCache(key.getVirtualECCId()).getIfPresent(key.getStartEpoch());
    }

    public void put(HistoryKey key, T value){
        getHistoryCache(key.getVirtualECCId()).put(key.getStartEpoch(), value);
    }
}
