package com.vpipl.leadmanagement.Utils;

import androidx.collection.LruCache;

/**
 * Created by mukesh on 25-05-2020.
 */

public class Cache {
    private static Cache instance;
    int cacheSize = 100 * 1024 * 1024;
    private LruCache<Object, Object> lru;

    private Cache() {

        lru = new LruCache<>(cacheSize);

    }

    public static Cache getInstance() {

        if (instance == null) {

            instance = new Cache();
        }

        return instance;

    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }
}