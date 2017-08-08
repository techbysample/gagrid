package com.nm.ignite.gagrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.transactions.Transaction;

import com.nm.ignite.gagrid.parameter.GAGridConstants;

/**
 * 
 * 
 * 
 * Responsible for performing truncate selection
 * 
 * @author turik.campbell
 *
 */
public class TruncateSelectionJob extends ComputeJobAdapter {

    /**
     * primary key of Chromosome to mutate
     */
    private Long key;

    /**
     * primary keys of genes to be used in mutation
     */
    private List<Long> mutatedGeneKeys;

    @IgniteInstanceResource
    private Ignite ignite = null;

    /**
     * 
     * @param key
     * @param mutatedGeneKeys primary keys of genes to be used in mutation
     *            
     * @param mutationRate
     */
    public TruncateSelectionJob(Long key, List<Long> mutatedGeneKeys) {
        this.key = key;
        this.mutatedGeneKeys = mutatedGeneKeys;
    }

    /**
     * Perform mutation
     */
    public Boolean execute() throws IgniteException {
        // TODO Auto-generated method stub

        IgniteCache<Long, Chromosome> populationCache = ignite.cache(GAGridConstants.POPULATION_CACHE);

        Chromosome chromosome = populationCache.localPeek(key);

        long[] geneKeys = chromosome.getGenes();

        for (int k = 0; k < this.mutatedGeneKeys.size(); k++) {
            {
                geneKeys[k] = this.mutatedGeneKeys.get(k);
            }
        }

        chromosome.setGenes(geneKeys);

        Transaction tx = ignite.transactions().txStart();

        populationCache.put(chromosome.id(), chromosome);

        tx.commit();

        return Boolean.TRUE;
    }

}
