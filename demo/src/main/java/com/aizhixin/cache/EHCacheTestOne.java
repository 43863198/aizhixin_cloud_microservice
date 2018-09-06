package com.aizhixin.cache;

//import org.ehcache.Cache;
//import org.ehcache.CacheManager;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.CacheManagerBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.config.units.MemoryUnit;
//
//public class EHCacheTestOne {
//
//    private CacheManager cacheManager;
//
//    public void init () {
//        try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//                .withCache("basicCache",
//                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(100).offheap(1, MemoryUnit.MB)))
//                .build(true)) {
//            Cache<Long, String> basicCache = cacheManager.getCache("basicCache", Long.class, String.class);
//
//            basicCache.put(1L, "da one!");
//            String value = basicCache.get(1L);
//
//            cacheManager.getCache("", Long.class, String.class);
//        }
//    }
//}
