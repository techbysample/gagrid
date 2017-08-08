package com.nm.ignite.gagrid;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.transactions.Transaction;

import com.nm.ignite.gagrid.parameter.GAGridConstants;

/**
 * Responsible for performing 'crossover' genetic operation for 2 X 'parent' chromosomes.
 * 
 * It relies on the GAConfiguration.getCrossOverRate() to determine probability rate of crossover for pair of
 * chromosome.
 * 
 * CrossOverJob will randomly pick a start index j in Chromosome.getGenes[] and continue
 * 
 * swapping until end of genes[] array.
 * 
 * @author turik.campbell
 * 
 * 
 */
public class CrossOverJob extends ComputeJobAdapter {

    @IgniteInstanceResource
    private Ignite ignite = null;

    @LoggerResource
    private IgniteLogger log = null;
    /**
     * primary key of 1st chromosome
     */
    private Long key1;

    /**
     * primary key of 2nd chromosome
     */
    private Long key2;

    /**
     * Cross over rate
     */
    private double crossOverRate;

    /**
     * 
     * @param primary key for 1st chromosome
     *            
     * @param primary key for 2nd chromosome
     *            
     * @param crossOverRate
     */
    public CrossOverJob(Long key1, Long key2, double crossOverRate) {
        this.key1 = key1;
        this.key2 = key2;
        this.crossOverRate = crossOverRate;
    }

    /**
     * Perform crossover operation
     */
    public Object execute() throws IgniteException {

        if (this.crossOverRate > Math.random()) {

            IgniteCache<Long, Chromosome> populationCache = ignite.cache(GAGridConstants.POPULATION_CACHE);

            Transaction tx = ignite.transactions().txStart();

            Chromosome chromosome1 = populationCache.localPeek(this.key1);
            Chromosome chromosome2 = populationCache.localPeek(this.key2);

            long[] genesforChrom1 = chromosome1.getGenes();
            long[] genesforChrom2 = chromosome2.getGenes();

            Random rn = new Random();

            // compute index to start for copying respective genes
            int geneIndexStartSwap = rn.nextInt(genesforChrom1.length);

            long[] newKeySwapArrayForChrome1 =
                Arrays.copyOfRange(genesforChrom2, geneIndexStartSwap, genesforChrom1.length);
            long[] newKeySwapArrayForChrome2 =
                Arrays.copyOfRange(genesforChrom1, geneIndexStartSwap, genesforChrom1.length);

            long[] newGeneKeysForChrom1 = crossOver(newKeySwapArrayForChrome1, geneIndexStartSwap, genesforChrom1);
            long[] newGeneKeysForChrom2 = crossOver(newKeySwapArrayForChrome2, geneIndexStartSwap, genesforChrom2);

            chromosome1.setGenes(newGeneKeysForChrom1);
            populationCache.put(chromosome1.id(), chromosome1);

            chromosome2.setGenes(newGeneKeysForChrom2);
            populationCache.put(chromosome2.id(), chromosome2);

            tx.commit();

        }

        return null;
    }

    /**
     * helper routine to assist cross over
     * 
     * @param newKeySwapArrayForChrome
     * @param updateIdx
     * @param genekeys
     * @return long[]
     */

    private long[] crossOver(long[] newKeySwapArrayForChrome, int updateIdx, long[] genekeys) {
        long[] newGeneKeys = genekeys.clone();

        int k = 0;
        for (int x = updateIdx; x < newGeneKeys.length; x++) {
            newGeneKeys[x] = newKeySwapArrayForChrome[k];
            k = k + 1;
        }
        return newGeneKeys;
    }

}
