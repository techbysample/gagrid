package com.nm.ignite.gagrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeJobResultPolicy;
import org.apache.ignite.compute.ComputeTaskAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;

import com.nm.ignite.gagrid.parameter.GAConfiguration;
import com.nm.ignite.gagrid.parameter.GAGridConstants;

/**
 *
 * 
 * Responsible for applying mutation on respective chromosomes.
 * 
 * MutateTask leverages Ignite's data affinity capabilities for routing MutateJobs to primary IgniteNode where
 * chromosomes reside.
 * 
 * 
 * @author turik.campbell
 *
 */
public class MutateTask extends ComputeTaskAdapter<List<Long>, Boolean> {

    @IgniteInstanceResource
    private Ignite ignite = null;

    private GAConfiguration config = null;

    /**
     * 
     * @param GAConfiguration
     */
    public MutateTask(GAConfiguration config) {
        this.config = config;
    }

    /**
     * @param ClusterNode
     * @param List<Long>
     *            - primary keys for respective chromosomes
     */
    public Map map(List<ClusterNode> nodes, List<Long> chromosomeKeys) throws IgniteException {

        Map<ComputeJob, ClusterNode> map = new HashMap<>();
        Affinity affinity = ignite.affinity(GAGridConstants.POPULATION_CACHE);

        for (Long key : chromosomeKeys) {
            MutateJob ajob = new MutateJob(key, getMutatedGenes(), this.config.getMutationRate());
            ClusterNode primary = affinity.mapKeyToNode(key);
            map.put(ajob, primary);
        }
        return map;
    }

    /**
     * choose mutated genes.
     * 
     * @return List<Long> - gene primary keys
     */
    private List<Long> getMutatedGenes() {
        List<Long> mutatedGenes = new ArrayList();
        config.getChromosomeLength();

        for (int i = 0; i < config.getChromosomeLength(); i++) {
            // Gene gene=config.getGenePool().get(selectRandomIndex(config.getGenePool().size()));
            mutatedGenes.add(selectGene(i));
        }

        return mutatedGenes;
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
     * @param k - gene index in Chromosome.
     *            
     * @return long
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
     * select an index at random
     * 
     * @param sizeOfGenePool
     * @return int - index
     */
    private int selectRandomIndex(int sizeOfGenePool) {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sizeOfGenePool);
        return index;
    }

    /**
     * We return TRUE if success, else Exection is thrown.
     * 
     * @param List<ComputeJobResult>
     * @return TRUE
     */
    public Boolean reduce(List<ComputeJobResult> list) throws IgniteException {
        return Boolean.TRUE;
    }

    /**
     * @param ComputeJobResult res
     * @param  List<ComputeJobResult> rcvd
     * 
     * @return ComputeJobResultPolicy
     */
    public ComputeJobResultPolicy result(ComputeJobResult res, List<ComputeJobResult> rcvd) {
        IgniteException err = res.getException();
  
        
        if (err != null)
            return ComputeJobResultPolicy.FAILOVER;
    
        // If there is no exception, wait for all job results.
        return ComputeJobResultPolicy.WAIT;
   
    }
    
}
