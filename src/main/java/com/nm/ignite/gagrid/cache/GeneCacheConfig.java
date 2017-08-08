package com.nm.ignite.gagrid.cache;

import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.configuration.CacheConfiguration;
import com.nm.ignite.gagrid.parameter.GAGridConstants;
import com.nm.ignite.gagrid.Chromosome;
import com.nm.ignite.gagrid.Gene;

/**
 * 
 * Cache configuration for GAGridConstants.GENE_CACHE
 * 
 * cache maintains full population of genes.
 * 
 * @author turik.campbell
 *
 */

public class GeneCacheConfig {

    /**
     * 
     * @return CacheConfiguration<Long, Gene>
     */
    public static CacheConfiguration<Long, Gene> geneCache() {

        CacheConfiguration<Long, Gene> cfg = new CacheConfiguration<>(GAGridConstants.GENE_CACHE);
        cfg.setIndexedTypes(Long.class, Gene.class);
        cfg.setCacheMode(CacheMode.REPLICATED);
        cfg.setRebalanceMode(CacheRebalanceMode.SYNC);
        cfg.setStatisticsEnabled(true);
        cfg.setBackups(1);
        return cfg;

    }

}
