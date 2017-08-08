package com.nm.ignite.gagrid.parameter;

import java.util.ArrayList;
import java.util.List;

import com.nm.ignite.gagrid.Gene;
import com.nm.ignite.gagrid.IFitnessFunction;

/**
 * 
 * 
 * Maintains configuration parameters to be used in genetic algorithm
 * 
 * <br/>
 * 
 * NOTE: Default selectionMethod is SELECTION_METHOD_TRUNCATION <br/>
 *       Default truncateRate is .10  <br/>
 *       
 *       More selectionMethods will be introduced in future releases. <br/>
 * 
 * 
 * 
 * 
 * @author turik.campbell
 * 
 *
 */
public class GAConfiguration {

    private GAGridConstants.SELECTION_METHOD selectionMethod = null;

    /**
     * Criteria used to describe a chromosome
     */
    private ChromosomeCriteria chromosomeCritiera = null;

    
    public GAConfiguration()
    {
        this.setSelectionMethod(GAGridConstants.SELECTION_METHOD.SELECTION_METHOD_TRUNCATION);
        this.setTruncateRate(.10);
    }
    /**
     * retrieve the ChromosomeCriteria
     * 
     * @return ChromosomeCriteria
     * 
     */
    public ChromosomeCriteria getChromosomeCritiera() {
        return chromosomeCritiera;
    }

    /**
     * set value for ChromosomeCriteria
     * 
     * @param chromosomeCritiera
     */

    public void setChromosomeCritiera(ChromosomeCriteria chromosomeCritiera) {
        this.chromosomeCritiera = chromosomeCritiera;
    }

    /**
     * Percentage of most fit chromosomes to be maintained and utilized to copy into new population.
     * 
     * NOTE: This parameter is only considered when selectionMethod is SELECTION_METHOD_TRUNCATION
     * 
     * Accepted values between 0 and 1
     */
    private double truncateRate;

    /**
     * Retrieve truncateRate
     * 
     * @return double
     *
     */
    public double getTruncateRate() {
        return truncateRate;
    }

    /**
     * Set truncatePercentage
     * 
     * @param double
     */
    public void setTruncateRate(double truncateRate) {
        this.truncateRate = truncateRate;
    }

    /**
     * Retrieve FITNESS_EVALUATER_TYPE
     * 
     * @return FITNESS_EVALUATER_TYPE
     */
    public GAGridConstants.FITNESS_EVALUATER_TYPE getFitnessEvaluator() {
        return fittnessEvaluator;
    }

    /**
     * Set value for FITNESS_EVALUATER_TYPE
     * 
     * @param FITNESS_EVALUATER_TYPE
     */
    public void setFitnessEvaluator(GAGridConstants.FITNESS_EVALUATER_TYPE fittnessIndicator) {
        this.fittnessEvaluator = fittnessEvaluator;
    }

    /**
     * Elitism is the concept that the strongest members of the population will be preserved from generation to
     * generation. <br/>
     * 
     * No crossovers or mutations will be performed for elite chromosomes.
     * 
     * NOTE: This parameter is only considered when selectionMethod is SELECTON_METHOD_ELETISM.
     * 
     */
    private int elitismCount = 0;

    /**
     * Indicates how chromosome fitness values should be evaluated
     * 
     * <br/>
     * IE: A chromosome with HIGHEST_FITNESS_VALUE_IS_FITTER is considered fittest. A chromosome with
     * LOWEST_FITNESS_VALUE_IS_FITTER is considered fittest.
     * 
     * 
     */
    private GAGridConstants.FITNESS_EVALUATER_TYPE fittnessEvaluator =
        GAGridConstants.FITNESS_EVALUATER_TYPE.HIGHEST_FITNESS_VALUE_IS_FITTER;

    /**
     * Population size represents the number of potential solutions (ie: chromosomes) between each generation
     * Default size is 500
     * 
     * </br>
     * NOTE: The population size remains fixed between each generation
     * 
     * 
     */
    private int populationSize = 500;

    /**
     * Gene pool is the sum of ALL genes utilized to create chromsomes.
     */
    private List<Gene> genePool = new ArrayList();

    /**
     * Number of genes within a chromosome
     */
    private int chromosomeLength = 0;

    /**
     * Crossover rate is the probability that two chromosomes will breed with each other. offspring with traits of each
     * of the parents. 
     * 
     * Accepted values are between 0 and 1
     */
    private double crossOverRate = .50;

    /**
     * Mutation rate is the probability that a chromosome will be mutated offspring with traits of each of the parents.
     * 
     * 
     * Accepted values are between 0 and 1
     */
    private double mutationRate = .50;

    /**
     * Call back interface used to terminate Genetic algorithm.
     * 
     * Implement this interface based on particular use case.
     */
    private ITerminateCriteria terminateCriteria = null;

    /**
     * Retrieve the mutation rate.
     * 
     * @return double
     */
    public double getMutationRate() {
        return mutationRate;
    }

    /**
     * Set the mutation rate.
     * 
     * @param double
     */
    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    /**
     * Represents a fitness function. Implement the IFitnessFunction to satisfy your particular use case.
     * 
     */
    private IFitnessFunction fitnessFunction = null;

    /**
     * Retrieve IFitnessFunction
     * 
     * @return IFitnessFunction
     */
    public IFitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    /**
     * Set IFitnessFunction
     * 
     * @param IFitnessFunction
     */
    public void setFitnessFunction(IFitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Retrieve the elitism count.
     * 
     * @return int.
     */
    public int getElitismCount() {
        return elitismCount;
    }

    /**
     * Set the elitism count.
     * 
     * @return int
     */
    public void setElitismCount(int elitismCount) {
        this.elitismCount = elitismCount;
    }

    /**
     * Retrieve the chromosome length
     * 
     * @return int
     */
    public int getChromosomeLength() {
        return chromosomeLength;
    }

    /**
     * 
     * @param chromosomeLength
     */
    public void setChromosomeLength(int chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }

    /**
     * Retrieve the gene pool.
     * 
     * @return List
     */
    public List<Gene> getGenePool() {
        return (this.genePool);
    }

    /**
     * Set the gene pool.
     * 
     * NOTE: When Apache Ignite is started the gene pool is utilized to initialize the distributed
     * GAGridConstants.GENE_CACHE.
     * 
     * @param genePool
     */
    public void setGenePool(List<Gene> genePool) {
        this.genePool = genePool;
    }

    /**
     * Retrieve the population size
     * 
     * @return int
     */

    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Set the population size
     * 
     * @param int
     */
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    /**
     * Retrieve the cross over rate.
     * 
     * @return double.
     */
    public double getCrossOverRate() {
        return crossOverRate;
    }

    /**
     * Set the cross over rate.
     * 
     * @param double.
     */
    public void setCrossOverRate(double crossOverRate) {
        this.crossOverRate = crossOverRate;
    }

    /**
     * Retreive the termination criteria.
     * 
     * @return ITerminateCriteria.
     */
    public ITerminateCriteria getTerminateCriteria() {
        return terminateCriteria;
    }

    /**
     * Set the termination criteria.
     * 
     * @param ITerminateCriteria.
     */
    public void setTerminateCriteria(ITerminateCriteria terminateCriteria) {
        this.terminateCriteria = terminateCriteria;
    }

    public GAGridConstants.SELECTION_METHOD getSelectionMethod() {
        return selectionMethod;
    }

    /**
     * set the selectionMethod
     * 
     * @param seletionMethod
     */
    public void setSelectionMethod(GAGridConstants.SELECTION_METHOD selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

}
