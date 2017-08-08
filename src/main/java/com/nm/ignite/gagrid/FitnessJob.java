package com.nm.ignite.gagrid;

import java.util.ArrayList;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.compute.ComputeExecutionRejectedException;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.transactions.Transaction;

import com.nm.ignite.gagrid.parameter.GAGridConstants;

/**
 * 
 * Responsible for performing fitness evaluation on an individual chromosome
 * 
 * @author turik.campbell
 *
 */
public class FitnessJob extends ComputeJobAdapter {

    /**
     * Chromosome primary Key
     */
    private Long key;

    @IgniteInstanceResource
    private Ignite ignite = null;

    @LoggerResource
    private IgniteLogger log = null;

    /**
     * IFitnessFunction
     */
    private IFitnessFunction fitnessFuncton = null;

    /**
     * 
     * @param key
     *            - Chromosome primary Key
     * @param fitnessFunction
     */
    public FitnessJob(Long key, IFitnessFunction fitnessFunction) {
        this.key = key;
        this.fitnessFuncton = fitnessFunction;
    }

    /**
     * Perform fitness operation utilizing IFitnessFunction
     * 
     * Update chromosome's fitness value
     * 
     */
    public Double execute() throws IgniteException {

        IgniteCache<Long, Chromosome> populationCache = ignite.cache(GAGridConstants.POPULATION_CACHE);

        IgniteCache<Long, Gene> geneCache = ignite.cache(GAGridConstants.GENE_CACHE);

        Chromosome chromosome = populationCache.localPeek(key);

        long[] geneKeys = chromosome.getGenes();

        List<Gene> genes = new ArrayList();

        for (int i = 0; i < geneKeys.length; i++) {
            long aKey = geneKeys[i];
            Gene aGene = geneCache.localPeek(aKey);
            genes.add(aGene);
        }

        Double value = fitnessFuncton.evaluate(genes);

        chromosome.setFitnessScore(value);

        Transaction tx = ignite.transactions().txStart();

        populationCache.put(chromosome.id(), chromosome);

        tx.commit();

        return value;
    }

}
