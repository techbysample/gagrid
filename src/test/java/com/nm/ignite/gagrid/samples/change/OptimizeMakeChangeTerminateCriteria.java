package com.nm.ignite.gagrid.samples.change;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;

import com.nm.ignite.gagrid.Chromosome;
import com.nm.ignite.gagrid.Gene;
import com.nm.ignite.gagrid.parameter.ITerminateCriteria;
import com.nm.ignite.gagrid.utils.GAGridUtils;
/**
 * 
 * Terminate Condition implementation for OptimizeMakeChangeGATest <br/>
 * 
 * 
 * @author turik.campbell
 *
 */
public class OptimizeMakeChangeTerminateCriteria implements ITerminateCriteria {

    private IgniteLogger igniteLogger = null;
    private Ignite ignite = null;

    public OptimizeMakeChangeTerminateCriteria(Ignite ignite) {
        this.ignite = ignite;
        this.igniteLogger = ignite.log();

    }

    public boolean isTerminationConditionMet(Chromosome fittestChromosome, double averageFitnessScore,
        int currentGeneration) {
        boolean isTerminate = true;

        igniteLogger.info("##########################################################################################");
        igniteLogger.info("Generation: " + currentGeneration);
        igniteLogger.info("Fittest is Chromosome Key: " + fittestChromosome);
        igniteLogger.info("Chromsome: " + fittestChromosome);
        printCoins(GAGridUtils.getGenesForChromosome(ignite, fittestChromosome));
        igniteLogger.info("Avg Chromsome Fitness: " + averageFitnessScore);
        igniteLogger.info("##########################################################################################");

        if (!(currentGeneration > 5)) {
            isTerminate = false;
        }

        return isTerminate;
    }

    /**
     * Helper to print change detail
     * 
     * @param genes
     */
    private void printCoins(List<Gene> genes) {
        for (Gene gene : genes) {
            igniteLogger.info("Coin Type: " + ((Coin) gene.getValue()).getCoinType().toString());
            igniteLogger.info("Number of Coins: " + ((Coin) gene.getValue()).getNumberOfCoins());
        }

    }
}
