package com.nm.ignite.gagrid.samples.helloworld;

import java.util.ArrayList;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nm.ignite.gagrid.Chromosome;
import com.nm.ignite.gagrid.GAGrid;
import com.nm.ignite.gagrid.Gene;
import com.nm.ignite.gagrid.parameter.GAConfiguration;
import com.nm.ignite.gagrid.parameter.GAGridConstants;

/**
 * This example demonstrates how to use the GAGrid framework.
 * 
 * In this example, we want to evolve a string of 11 characters such that the word 'HELLO WORLD'.
 * 
 * is found.
 * 
 * 
 * How To Run:
 * 
 * mvn test -Dtest=HelloWorldGATest -Dignite.version=${ignite.version}
 * 
 */

public class HelloWorldGATest {
    private Ignite ignite = null;
    private GAGrid gaGrid = null;
    private GAConfiguration gaConfig = null;

    @Before
    public void initialize() {
       System.setProperty("IGNITE_QUIET", "false");

       
        try {
            
           // Create an Ignite instance as you would in any other use case.
            ignite = Ignition.start("config/gagrid-config.xml");
            
            // Create GAConfiguration
            gaConfig = new GAConfiguration();
 
            // set Gene Pool
            List<Gene> genes = this.getGenePool();

            // set the Chromosome Length to '11' since 'HELLO WORLD' contains 11 characters.
            gaConfig.setChromosomeLength(11);

            // initialize gene pool
            gaConfig.setGenePool(genes);

            // create and set Fitness function
            HelloWorldFitnessFunction function = new HelloWorldFitnessFunction();
            gaConfig.setFitnessFunction(function);

            // create and set TerminateCriteria
            HelloWorldTerminateCriteria termCriteria = new HelloWorldTerminateCriteria(ignite);
            gaConfig.setTerminateCriteria(termCriteria);

            ignite.log();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Test
    public void testEvolve() {
        // initialize GAGrid
        gaGrid = new GAGrid(gaConfig, ignite);
        // evolve the population
        Chromosome fittestChromosome = gaGrid.evolve();
    }

    @After
    public void tearDown() {
        ignite = null;
    }

    /**
     * Helper routine to initialize Gene pool
     * 
     * In typical usecase genes may be stored in database.
     * 
     * @return List<Gene>
     */
    private List<Gene> getGenePool() {
        List<Gene> list = new ArrayList();

        char[] chars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ' };

        for (int i = 0; i < chars.length; i++) {
            Gene gene = new Gene(new Character(chars[i]));
            list.add(gene);
        }
        return list;
    }

}
