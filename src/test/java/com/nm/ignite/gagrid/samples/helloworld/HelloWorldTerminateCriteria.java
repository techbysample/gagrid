package com.nm.ignite.gagrid.samples.helloworld;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;

import com.nm.ignite.gagrid.Chromosome;
import com.nm.ignite.gagrid.Gene;
import com.nm.ignite.gagrid.parameter.ITerminateCriteria;
import com.nm.ignite.gagrid.utils.GAGridUtils;

/**
 * Represents the terminate condition for HelloWorld Genetic algorithm
 * 
 * Class terminates Genetic algorithm when fitnessScore > 10 
 * 
 * 
 * @author turik.campbell
 *
 */
public class HelloWorldTerminateCriteria implements ITerminateCriteria {

    private IgniteLogger igniteLogger = null;
    private Ignite ignite = null;
    
    public HelloWorldTerminateCriteria(Ignite ignite) {
        this.ignite = ignite;
        this.igniteLogger = ignite.log();

    }

    public boolean isTerminationConditionMet(Chromosome fittestChromosome, double averageFitnessScore, int currentGeneration) {
        boolean isTerminate = true;

        igniteLogger.info("##########################################################################################");
        igniteLogger.info("Generation: " + currentGeneration);
        igniteLogger.info("Fittest is Chromosome Key: " + fittestChromosome);
        igniteLogger.info("Chromosome: " + fittestChromosome);
        printPhrase(GAGridUtils.getGenesInOrderForChromosome(ignite, fittestChromosome));
        igniteLogger.info("Avg Chromosome Fitness: " + averageFitnessScore);
        igniteLogger.info("##########################################################################################");

        if (!(fittestChromosome.getFitnessScore() > 10)) {
            isTerminate = false;
        }
        
        return isTerminate;
    }
    
    
    /**
     * Helper to print Phrase
     * 
     * @param genes
     */
    private void printPhrase(List<Gene> genes) {
        
        StringBuffer sbPhrase = new StringBuffer();
        
        for (Gene gene : genes) {
            sbPhrase.append(((Character) gene.getValue()).toString());
        }
        igniteLogger.info(sbPhrase.toString());
    }

}
