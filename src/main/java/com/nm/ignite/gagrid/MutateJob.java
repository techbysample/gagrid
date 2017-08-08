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
 * Responsible for applying mutation on respective Chromosome based on mutation Rate
 * 
 * @author turik.campbell
 *
 */
public class MutateJob extends ComputeJobAdapter {

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
     * Mutation Rate
     */
    private double mutationRate;

    /**
     * 
     * @param key
     * @param mutatedGeneKeys
     *            primary keys of genes to be used in mutation
     * @param mutationRate
     */
    public MutateJob(Long key, List<Long> mutatedGeneKeys, double mutationRate) {
        this.key = key;
        this.mutationRate = mutationRate;
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
            // Mutate gene based on MutatonRate
            if (this.mutationRate > Math.random()) {
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
