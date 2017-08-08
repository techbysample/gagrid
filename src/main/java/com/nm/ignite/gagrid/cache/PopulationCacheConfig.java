package com.nm.ignite.gagrid.cache;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.configuration.CacheConfiguration;

import com.nm.ignite.gagrid.Chromosome;
import com.nm.ignite.gagrid.parameter.GAGridConstants;

/**
 * 
 * Cache configuration for GAGridConstants.POPULATION_CACHE
 * 
 * cache population of chromosomes (ie: potential solutions)
 * 
 * 
 * 
 * @author turik.campbell.ms
 *
 */

public class PopulationCacheConfig {

    /**
     * 
     * @return CacheConfiguration<Long, Chromosome>
     */
    public static CacheConfiguration<Long, Chromosome> populationCache() {

        CacheConfiguration<Long, Chromosome> cfg = new CacheConfiguration<>(GAGridConstants.POPULATION_CACHE);
        cfg.setIndexedTypes(Long.class, Chromosome.class);
        cfg.setCacheMode(CacheMode.PARTITIONED);
        cfg.setRebalanceMode(CacheRebalanceMode.SYNC);
        cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        cfg.setStatisticsEnabled(true);
        cfg.setBackups(1);

        return cfg;

    }

}
