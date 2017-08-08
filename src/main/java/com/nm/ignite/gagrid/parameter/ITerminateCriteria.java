package com.nm.ignite.gagrid.parameter;

import com.nm.ignite.gagrid.Chromosome;

/**
 * 
 * Represents the terminate condition for a genetic algorithm.
 * 
 * Implement this interface for your respective use case.
 * 
 * @author turik.campbell
 *
 */
public interface ITerminateCriteria {
    /**
     * 
     * @param fittestChromosome  fittest chromosome as of the nth generation
     *            
     * @param generation  current number of generations
     *           
     * @return boolean
     */
    public boolean isTerminationConditionMet(Chromosome fittestChromosome, double averageFitnessScore, int generation);
}
