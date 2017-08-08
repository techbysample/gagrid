package com.nm.ignite.gagrid.samples.movie;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;

import com.nm.ignite.gagrid.Chromosome;
import com.nm.ignite.gagrid.Gene;
import com.nm.ignite.gagrid.parameter.ITerminateCriteria;
import com.nm.ignite.gagrid.samples.change.Coin;
import com.nm.ignite.gagrid.utils.GAGridUtils;

/**
 * Represents the terminate condition for Movie Genetic algorithm  <br/>
 * 
 * Class terminates Genetic algorithm when fitnessScore > 32  <br/>
 *  
 * 
 * <br/>
 * 
 * @author turik.campbell
 *
 */

public class MovieTerminateCriteria implements ITerminateCriteria {

    private IgniteLogger igniteLogger = null;
    private Ignite ignite = null;

    public MovieTerminateCriteria(Ignite ignite) {
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
        printMovies(GAGridUtils.getGenesForChromosome(ignite, fittestChromosome));
        igniteLogger.info("##########################################################################################");


        if (!(fittestChromosome.getFitnessScore() > 32)) {
            isTerminate = false;
        }

        return isTerminate;
    }

    /**
     * Helper to print change detail
     * 
     * @param genes
     */
    private void printMovies(List<Gene> genes) {
        for (Gene gene : genes) {
            igniteLogger.info("Name: " + ((Movie) gene.getValue()).getName().toString());
            igniteLogger.info("Genres: " + ((Movie) gene.getValue()).getGenre().toString());
            igniteLogger.info("IMDB Rating: " + ((Movie) gene.getValue()).getImdbRating());
        }

    }
    
}
