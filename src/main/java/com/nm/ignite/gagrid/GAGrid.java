package com.nm.ignite.gagrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;

import com.nm.ignite.gagrid.cache.GeneCacheConfig;
import com.nm.ignite.gagrid.cache.PopulationCacheConfig;
import com.nm.ignite.gagrid.parameter.GAConfiguration;
import com.nm.ignite.gagrid.parameter.GAGridConstants;
import com.nm.ignite.gagrid.utils.GAGridUtils;

/**
 * 
 * Central class responsible for orchestrating the Genetic Algorithm.
 * 
 * This class accepts a GAConfigriation and Ignite instance. <br/>
 * 
 * <img src="../../../../images/GAProcessDiagram.png"/>
 * 
 * <br/>
 * 
 * 
 * @author turik.campbell
 *
 */
public class GAGrid {

    /*
     * GAConfiguraton
     */
    private GAConfiguration config = null;

    /**
     * Ignite instance
     */
    private Ignite ignite = null;
    private IgniteCache<Long, Chromosome> populationCache = null;
    private IgniteCache<Long, Gene> geneCache = null;
    private List<Long> populationKeys = new ArrayList();

    IgniteLogger igniteLogger = null;

    /**
     * 
     * @param GAConfiguration
     * @param Ignite
     */
    public GAGrid(GAConfiguration config, Ignite ignite) {
        this.ignite = ignite;
        this.config = config;
        this.ignite = ignite;
        this.igniteLogger = ignite.log();
        populationCache = this.ignite.getOrCreateCache(PopulationCacheConfig.populationCache());
        geneCache = this.ignite.getOrCreateCache(GeneCacheConfig.geneCache());
    }

    /**
     * Evolve the population
     * 
     * @return Chromosome
     */
    public Chromosome evolve() {
        // keep track of current generation
        int generationCount = 1;

        Chromosome fittestChomosome = null;

        initializeGenePopulation();

        intializePopulation();

        // Calculate Fitness
        calculateFitness(this.populationKeys);

        // Retrieve chromosomes in order by fitness value
        List<Long> keys = getChromosomesByFittest();

        // Calculate average fitness value of population
        double averageFitnessScore = calculateAverageFitness();

        fittestChomosome = populationCache.get(keys.get(0));

        List<Gene> genes = GAGridUtils.getGenesForChromosome(ignite, fittestChomosome);

        // while NOT terminateCondition met
        while (!(config.getTerminateCriteria().isTerminationConditionMet(fittestChomosome, averageFitnessScore,
            generationCount))) {
            generationCount = generationCount + 1;

            // We will crossover/mutate over chromosomes based on selection method

            List<Long> selectedKeysforCrossMutaton = selection(keys);

            // Cross Over
            crossover(selectedKeysforCrossMutaton);

            // Mutate
            mutation(selectedKeysforCrossMutaton);

            // Calculate Fitness
            calculateFitness(selectedKeysforCrossMutaton);

            // Retrieve chromosomes in order by fitness value
            keys = getChromosomesByFittest();

            // Retreive the first chromosome from the list
            fittestChomosome = populationCache.get(keys.get(0));

            // Calculate average fitness value of population
            averageFitnessScore = calculateAverageFitness();

            // End Loop

        }
        return fittestChomosome;
    }

    /**
     * Select chromosomes.
     * 
     * @param chromosomeKeys
     * @return
     */

    private List<Long> selection(List<Long> chromosomeKeys) {
        List<Long> selectedKeys = new ArrayList();

        GAGridConstants.SELECTION_METHOD selectionMethod = config.getSelectionMethod();

        switch (selectionMethod) {
            case SELECTON_METHOD_ELETISM:
                selectedKeys = selectByElitism(chromosomeKeys);
                break;
            case SELECTION_METHOD_TRUNCATION:
                selectedKeys = selectByTruncation(chromosomeKeys);

                List<Long> fittestKeys = getFittestKeysForTruncation(chromosomeKeys);

                Boolean boolValue = copyFitterChromosomesToPopulation(fittestKeys, selectedKeys);

                // copy more fit keys to rest of population
                break;

            default:
                break;
        }

        return selectedKeys;
    }

    /**
     * 
     * @param fittestKeys
     *            -chromosomeKeys that will be copied from.
     * @param selectedKeys
     *            - chromosomeKeys that will be overwritten evenly by fittestKeys
     * @return
     */
    private Boolean copyFitterChromosomesToPopulation(List<Long> fittestKeys, List<Long> selectedKeys) {
        double truncatePercentage = this.config.getTruncateRate();

        int totalSize = this.populationKeys.size();

        int truncateCount = (int) (truncatePercentage * totalSize);

        int numberOfCopies = selectedKeys.size() / truncateCount;

        Boolean boolValue = this.ignite.compute()
            .execute(new TruncateSelectionTask(this.config, fittestKeys, numberOfCopies), selectedKeys);

        return Boolean.TRUE;

    }

    private List<Long> getFittestKeysForTruncation(List<Long> keys) {
        double truncatePercentage = this.config.getTruncateRate();

        int truncateCount = (int) (truncatePercentage * keys.size());

        List<Long> selectedKeysToRetain = keys.subList(0, truncateCount);

        return selectedKeysToRetain;
    }

    /**
     * Truncation selection simply retains the fittest x% of the population. These fittest individuals are duplicated so
     * that the population size is maintained.
     * 
     * 
     * @param keys
     * @return
     */

    private List<Long> selectByTruncation(List<Long> keys) {
        double truncatePercentage = this.config.getTruncateRate();

        int truncateCount = (int) (truncatePercentage * keys.size());

        List<Long> selectedKeysForCrossOver = keys.subList(truncateCount, keys.size());

        return selectedKeysForCrossOver;
    }

    /**
     * For our implementation we consider 'best fit' chromosomes, by selecting least fit chromosomes for crossover and
     * mutation
     * 
     * As result, we are interested in least fit chromosomes.
     * 
     * @param keys
     */
    private List<Long> selectByElitism(List<Long> keys) {
        int elitismCount = this.config.getElitismCount();
        List<Long> leastFitKeys = keys.subList(elitismCount, keys.size());
        return leastFitKeys;
    }

    private void mutation(List<Long> leastFitKeys) {
        Boolean boolValue = this.ignite.compute().execute(new MutateTask(this.config), leastFitKeys);

    }

    private void crossover(List<Long> leastFitKeys) {
        Boolean boolValue = this.ignite.compute().execute(new CrossOverTask(this.config), leastFitKeys);
    }

    /**
     * Calculate fitness each Chromosome in population
     * 
     * @return
     */
    private void calculateFitness(List<Long> chromosomeKeys) {
        Boolean boolValue = this.ignite.compute().execute(new FitnessTask(this.config), chromosomeKeys);
    }

    /**
     * Initialize the population of Chromosomes
     */
    private void initializePopulation() {
        int populationSize = config.getPopulationSize();
        populationCache.clear();

        for (int j = 0; j < populationSize; j++) {
            Chromosome chromosome = createChromosome(config.getChromosomeLength());
            populationCache.put(chromosome.id(), chromosome);
            populationKeys.add(chromosome.id());
        }

    }

    /**
     * initialize the Gene pool
     */
    private void initializeGenePopulation() {
        geneCache.clear();

        List<Gene> genePool = config.getGenePool();

        for (Gene gene : genePool) {
            geneCache.put(gene.id(), gene);
        }
    }

    /**
     * initialize the population of Chromosomes based on GAConfiguration
     */
    private void intializePopulation() {
        int populationSize = config.getPopulationSize();
        populationCache.clear();

        for (int j = 0; j < populationSize; j++) {
            Chromosome chromosome = createChromosome(config.getChromosomeLength());
            populationCache.put(chromosome.id(), chromosome);
            populationKeys.add(chromosome.id());
        }

    }

    /**
     * create a Chromsome
     * 
     * @param numberOfGenes
     * @return
     */
    private Chromosome createChromosome(int numberOfGenes) {
        long[] genes = new long[numberOfGenes];
        List<Long> keys = new ArrayList();
        int k = 0;
        while (k < numberOfGenes) {
            long key = selectGene(k);

            if (!(keys.contains(key))) {
                genes[k] = key;
                keys.add(key);
                k = k + 1;
            }
        }
        Chromosome aChromsome = new Chromosome(genes);
        return aChromsome;
    }

    /**
     * 
     * @param k
     *            - gene index in Chromosome.
     * @return
     */
    private long selectGene(int k) {
        if (config.getChromosomeCritiera() == null) {
            return (selectAnyGene());
        } else {
            return (selectGeneByChromsomeCriteria(k));
        }
    }

    /**
     * method assumes ChromosomeCriteria is set.
     * 
     * @param k
     *            - gene index in Chromosome.
     * @return
     */
    private long selectGeneByChromsomeCriteria(int k) {
        List<Gene> genes = new ArrayList();

        StringBuffer sbSqlClause = new StringBuffer("_val like '");
        sbSqlClause.append("%");
        sbSqlClause.append(config.getChromosomeCritiera().getCriteria().get(k));
        sbSqlClause.append("%'");

        IgniteCache<Long, Gene> cache = ignite.cache(GAGridConstants.GENE_CACHE);

        SqlQuery sql = new SqlQuery(Gene.class, sbSqlClause.toString());

        try (QueryCursor<Entry<Long, Gene>> cursor = cache.query(sql)) {
            for (Entry<Long, Gene> e : cursor)
                genes.add(e.getValue());
        }

        int idx = selectRandomIndex(genes.size());

        Gene gene = genes.get(idx);
        return gene.id();
    }

    /**
     * select a gene from the Gene pool
     * 
     * @return
     */
    private long selectAnyGene() {
        int idx = selectRandomIndex(config.getGenePool().size());
        Gene gene = config.getGenePool().get(idx);
        return gene.id();
    }

    private int selectRandomIndex(int sizeOfGenePool) {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sizeOfGenePool);
        return index;
    }

    /**
     * helper routine to retrieve Chromosome keys in order of fittest
     * 
     * @return
     */
    private List<Long> getChromosomesByFittest() {
        List<Long> orderChromKeysByFittest = new ArrayList();
        String orderDirection = "desc";

        GAGridConstants.FITNESS_EVALUATER_TYPE evaluatorType = config.getFitnessEvaluator();

        switch (evaluatorType) {
            case LOWEST_FITNESS_VALUE_IS_FITTER:
                orderDirection = "asc";
                break;
            default:
                break;
        }
        String fittestSQL = "select _key from Chromosome order by fitnessScore " + orderDirection;

        // Execute query to keys for ALL Chromosomes by fittnessScore
        QueryCursor<List<?>> cursor = populationCache.query(new SqlFieldsQuery(fittestSQL));

        List<List<?>> res = cursor.getAll();

        for (List row : res) {
            Long key = (Long) row.get(0);
            orderChromKeysByFittest.add(key);
        }

        return orderChromKeysByFittest;
    }

    /**
     * Calculate average fitness value
     * 
     * @return Double
     */

    private Double calculateAverageFitness() {

        double avgFitnessScore = 0;

        IgniteCache<Long, Gene> cache = ignite.cache(GAGridConstants.POPULATION_CACHE);

        // Execute query to get names of all employees.
        SqlFieldsQuery sql = new SqlFieldsQuery("select AVG(FITNESSSCORE) from Chromosome");

        // Iterate over the result set.
        try (QueryCursor<List<?>> cursor = cache.query(sql)) {
            for (List<?> row : cursor)
                avgFitnessScore = (Double) row.get(0);
        }

        return avgFitnessScore;
    }
}
