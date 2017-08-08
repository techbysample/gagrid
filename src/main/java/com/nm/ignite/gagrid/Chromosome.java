package com.nm.ignite.gagrid;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * 
 * Represents a potential solution consisting of a fixed-length collection of genes. <br/>
 * 
 * NOTE: Chromosome resides in cache: GAGridConstants.POPULATION_CACHE. This cached is partitioned.
 * 
 * @author turik.campbell
 *
 */
public class Chromosome {

    private static final AtomicLong ID_GEN = new AtomicLong();

    @QuerySqlField(index = true)
    private double fitnessScore = -1;

    /** Id (indexed). */
    @QuerySqlField(index = true)
    private Long id;

    /**
     * array of gene keys.
     */
    private long[] genes;

    public Chromosome(long[] genes) {
        id = ID_GEN.incrementAndGet();
        this.genes = genes;
    }

    /**
     * Retrieve gene keys (ie: primary keys) for this chromosome
     * 
     * @return long[]
     */
    public long[] getGenes() {
        return genes;
    }

    /**
     * Set the gene keys (ie: primary keys)
     * 
     * @param genes
     */
    public void setGenes(long[] genes) {
        this.genes = genes;
    }

    /**
     * Return the id (primary key) for this chromosome
     * 
     * @return Long
     */
    public Long id() {
        return id;
    }

    /**
     * Return the fitnessScore
     * 
     * @return double
     */
    public double getFitnessScore() {
        return fitnessScore;
    }

    /**
     * Set the fitnessScore
     * 
     * @param double
     */
    public void setFitnessScore(double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    @Override
    public String toString() {
        return "Chromosome [fitnessScore=" + fitnessScore + ", id=" + id + ", genes=" + Arrays.toString(genes) + "]";
    }

}
