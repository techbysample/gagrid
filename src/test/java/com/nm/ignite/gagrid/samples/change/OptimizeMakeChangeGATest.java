package com.nm.ignite.gagrid.samples.change;

import java.util.ArrayList;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nm.ignite.gagrid.Chromosome;
import com.nm.ignite.gagrid.GAGrid;
import com.nm.ignite.gagrid.Gene;
import com.nm.ignite.gagrid.parameter.ChromosomeCriteria;
import com.nm.ignite.gagrid.parameter.GAConfiguration;
import com.nm.ignite.gagrid.parameter.GAGridConstants;

import junit.framework.Assert;

/**
 * This example demonstrates how to use the GAGrid framework. <br/>
 * 
 * This example is inspired by JGAP's "Minimize Make Change" example. <br/>
 * 
 * In this example, the objective is to calculate the minimum number of coins that equal user specified amount of change
 * 
 * ie: -DAMOUNTCHANGE
 * 
 * How to run: <br/>
 * 
 * <br/>
 * 
 * mvn test -Dtest=OptimizeMakeChangeGATest -Dignite.version={ignite.version} -DAMOUNTCHANGE=75
 * 
 * <br/>
 * @author turik.campbell
 */

public class OptimizeMakeChangeGATest {
    private Ignite ignite = null;
    private GAGrid gaGrid = null;
    private GAConfiguration gaConfig = null;

    private String sAmountChange=null;
    
    private IgniteLogger logger = null;
    @Before
    public void initialize() {
        System.setProperty("IGNITE_QUIET", "false");

         sAmountChange= System.getProperty("AMOUNTCHANGE");
        
        StringBuffer sbErrorMessage = new StringBuffer();
        sbErrorMessage.append("AMOUNTCHANGE System property not set. Please provide a valid value between 1 and 99. ");
        sbErrorMessage.append(" ");
        sbErrorMessage.append("IE: -DAMOUNTCHANGE=75");
      
        Assert.assertTrue("AMOUNTCHANGE value should be greater than or equal to 1",Integer.parseInt(sAmountChange) >= 1);
        Assert.assertTrue("AMOUNTCHANGE value should be less than or equal to 99",Integer.parseInt(sAmountChange) <= 99);
       
        Assert.assertNotNull(sbErrorMessage.toString(), sAmountChange);
        
        try {

            // Create an Ignite instance as you would in any other use case.
            ignite = Ignition.start("config/gagrid-config.xml");

            logger = ignite.log();
            
            IgniteConfiguration config = ignite.configuration();

            // Create GAConfiguration
            gaConfig = new GAConfiguration();

            // set Gene Pool
            List<Gene> genes = this.getGenePool();

            // set selection method
            gaConfig.setSelectionMethod(GAGridConstants.SELECTION_METHOD.SELECTION_METHOD_TRUNCATION);

            // set the Chromosome Length to '4' since we have 4 coins.
            gaConfig.setChromosomeLength(4);

            // set population size
            gaConfig.setPopulationSize(500);

            // initialize gene pool
            gaConfig.setGenePool(genes);

            // set Truncate Rate
            gaConfig.setTruncateRate(.10);

            // set Cross Over Rate
            gaConfig.setCrossOverRate(.50);

            // set Mutation Rate
            gaConfig.setMutationRate(.50);

            // create and set Fitness function
            OptimizeMakeChangeFitnessFunction function = new OptimizeMakeChangeFitnessFunction(new Integer(sAmountChange));
            gaConfig.setFitnessFunction(function);

            // create and set TerminateCriteria
            OptimizeMakeChangeTerminateCriteria termCriteria = new OptimizeMakeChangeTerminateCriteria(ignite);

            ChromosomeCriteria chromosomeCritiera = new ChromosomeCriteria();

            List values = new ArrayList();

            values.add("coinType=QUARTER");
            values.add("coinType=DIME");
            values.add("coinType=NICKEL");
            values.add("coinType=PENNY");

            chromosomeCritiera.setCriteria(values);

            gaConfig.setChromosomeCritiera(chromosomeCritiera);
            gaConfig.setTerminateCriteria(termCriteria);

            // initialize GAGrid
            gaGrid = new GAGrid(gaConfig, ignite);

        
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Test
    public void testEvolve() {
    	
    	logger.info("##########################################################################################");
        
    	logger.info("Calculating optimal set of coins where amount of change is " + this.sAmountChange);
    	
    	logger.info("##########################################################################################");
        
    	
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

        Gene quarterGene1 = new Gene(new Coin(Coin.CoinType.QUARTER, 3));
        Gene quarterGene2 = new Gene(new Coin(Coin.CoinType.QUARTER, 2));
        Gene quarterGene3 = new Gene(new Coin(Coin.CoinType.QUARTER, 1));
        Gene quarterGene4 = new Gene(new Coin(Coin.CoinType.QUARTER, 0));

        Gene dimeGene1 = new Gene(new Coin(Coin.CoinType.DIME, 2));
        Gene dimeGene2 = new Gene(new Coin(Coin.CoinType.DIME, 1));
        Gene dimeGene3 = new Gene(new Coin(Coin.CoinType.DIME, 0));

        Gene nickelGene1 = new Gene(new Coin(Coin.CoinType.NICKEL, 1));
        Gene nickelGene2 = new Gene(new Coin(Coin.CoinType.NICKEL, 0));

        Gene pennyGene1 = new Gene(new Coin(Coin.CoinType.PENNY, 4));
        Gene pennyGene2 = new Gene(new Coin(Coin.CoinType.PENNY, 3));
        Gene pennyGene3 = new Gene(new Coin(Coin.CoinType.PENNY, 2));
        Gene pennyGene4 = new Gene(new Coin(Coin.CoinType.PENNY, 1));
        Gene pennyGene5 = new Gene(new Coin(Coin.CoinType.PENNY, 0));

        list.add(quarterGene1);
        list.add(quarterGene2);
        list.add(quarterGene3);
        list.add(quarterGene4);
        list.add(dimeGene1);
        list.add(dimeGene2);
        list.add(dimeGene3);
        list.add(nickelGene1);
        list.add(nickelGene2);
        list.add(pennyGene1);
        list.add(pennyGene2);
        list.add(pennyGene3);
        list.add(pennyGene4);
        list.add(pennyGene5);

        return list;
    }

}
