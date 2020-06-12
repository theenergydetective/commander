/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;


import com.ted.commander.server.model.energyPost.EnergyPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This is a simple in memory queue that is used to prevent the same ECC's energy post from being posted at the same time.
 */

@Service
public class EnergyPostQueue {
    static final Logger LOGGER = LoggerFactory.getLogger(EnergyPostQueue.class);
    HashMap<String, LinkedList<EnergyPost>> postMap = new HashMap<>(1024);

    public synchronized void addPost(EnergyPost energyPost) {
        LinkedList<EnergyPost> linkedList = postMap.get(energyPost.getGateway());
        if (linkedList == null) {
            linkedList = new LinkedList<>();
            postMap.put(energyPost.getGateway(), linkedList);
        }
        linkedList.add(energyPost);
    }

    /**
     * Returns true if more entries exist
     *
     * @param eccId
     * @return
     */
    public synchronized EnergyPost popPost(String eccId) {
        try {
            LinkedList<EnergyPost> linkedList = postMap.get(eccId);
            if (linkedList != null) {
                if (linkedList.size() == 1) {
                    postMap.remove(eccId);
                    return null;
                } else {
                    linkedList.pop();
                    EnergyPost energyPost = linkedList.peekFirst();
                    return energyPost;
                }
            }
        } catch (Exception ex){
            LOGGER.error("unknown exception", ex);
            postMap.remove(eccId);
        }
        return null;
    }

    public synchronized boolean isProcessing(String eccId) {
        return size(eccId) > 0;
    }

    public int size(String eccId) {
        LinkedList<EnergyPost> linkedList = postMap.get(eccId);
        if (linkedList != null) {
            return linkedList.size();
        }
        return 0;
    }
}
