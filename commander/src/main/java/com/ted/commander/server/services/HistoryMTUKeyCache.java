package com.ted.commander.server.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.history.HistoryMTUKey;
import com.ted.commander.server.dao.CacheRemovalListener;

import java.util.concurrent.TimeUnit;

public class HistoryMTUKeyCache<T> {

    Cache<Long, Cache<Long, Cache<Long, T>>> virtualECCCache;
    final int size;
    final int duration;
    final TimeUnit timeUnit;

    public HistoryMTUKeyCache(int size, int duration, TimeUnit timeUnit){
        virtualECCCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.DAYS).maximumSize(5000).initialCapacity(2000).removalListener(new CacheRemovalListener()).build();
        this.size =size;
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    private Cache<Long, T> getHistoryCache(Long virtualECCId, Long mtuId){
        Cache<Long, Cache<Long, T>> mtuCache = virtualECCCache.getIfPresent(virtualECCId);

        if (mtuCache == null){
            mtuCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.DAYS).maximumSize(2000).initialCapacity(500).removalListener(new CacheRemovalListener()).build();
            virtualECCCache.put(virtualECCId, mtuCache);
        }

        Cache<Long, T> cache = mtuCache.getIfPresent(mtuId);
        if (cache == null){
            cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(duration, timeUnit).maximumSize(size).initialCapacity(size).build();
            mtuCache.put(mtuId, cache);
        }
        return cache;
    }

    public void invalidate(HistoryMTUKey key){
        getHistoryCache(key.getVirtualECCId(), key.getMtuId()).invalidate(key.getStartEpoch());
    }

    public T get(HistoryMTUKey key){
        return getHistoryCache(key.getVirtualECCId(), key.getMtuId()).getIfPresent(key.getStartEpoch());
    }

    public void put(HistoryMTUKey key, T value){
        getHistoryCache(key.getVirtualECCId(), key.getMtuId()).put(key.getStartEpoch(), value);
    }
}
