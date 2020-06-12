package com.ted.commander.server.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ted.commander.common.model.history.HistoryBillingCycleKey;
import com.ted.commander.common.model.history.HistoryKey;
import com.ted.commander.server.dao.CacheRemovalListener;

import java.util.concurrent.TimeUnit;

public class HistoryBillingCycleKeyCache<T> {

    Cache<Long, Cache<Long, T>> virtualECCCache;
    final int size;
    final int duration;
    final TimeUnit timeUnit;

    public HistoryBillingCycleKeyCache(int size, int duration, TimeUnit timeUnit){
        virtualECCCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.DAYS).maximumSize(5000).initialCapacity(2000).removalListener(new CacheRemovalListener()).build();
        this.size =size;
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    private Cache<Long, T> getHistoryCache(Long virtualECCId){
        Cache<Long, T> cache = virtualECCCache.getIfPresent(virtualECCId);
        if (cache == null){
            cache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterWrite(duration, timeUnit).maximumSize(size).initialCapacity(size).build();
            virtualECCCache.put(virtualECCId, cache);
        }
        return cache;
    }

    private long getKey(HistoryBillingCycleKey key){
        long keyValue = (key.getBillingCycleYear() * 100);
        keyValue += key.getBillingCycleMonth();
        return keyValue;
    }

    public void invalidate(HistoryBillingCycleKey key){
        getHistoryCache(key.getVirtualECCId()).invalidate(getKey(key));
    }

    public T get(HistoryBillingCycleKey key){
        return getHistoryCache(key.getVirtualECCId()).getIfPresent(getKey(key));
    }

    public void put(HistoryBillingCycleKey key, T value){
        getHistoryCache(key.getVirtualECCId()).put(getKey(key), value);
    }
}
