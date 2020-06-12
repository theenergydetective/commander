package com.ted.commander.server.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class CacheRemovalListener implements RemovalListener<Long, Cache> {
    @Override
    public void onRemoval(RemovalNotification<Long, Cache> removalNotification) {
        removalNotification.getValue().invalidateAll();
    }
}
