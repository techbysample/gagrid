package com.nm.ignite.gagrid;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.affinity.Affinity;
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
 * Responsible for assigning 2 X 'parent' chromosomes to produce 2 X 'child' chromosomes. CrossOverTask leverages
 * Ignite's data affinity capabilities for routing CrossOverJobs to primary IgniteNode where 'parent' chromosomes
 * reside.
 * 
 * @author turik.campbell
 *
 */

public class CrossOverTask extends ComputeTaskAdapter<List<Long>, Boolean> {

    @IgniteInstanceResource
    private Ignite ignite = null;

    /**
     * GAConfiguration
     */
    private GAConfiguration config = null;

    /**
     * 
     * @param GAConfiguration
     */
    public CrossOverTask(GAConfiguration config) {
        this.config = config;
    }

    /**
     * 
     * Map Jobs to nodes using data affinity.
     * 
     * @param ClusterNode
     * @param List<Long> primary keys for respective chromosomes
     * 
     * 
     */
    public Map map(List<ClusterNode> nodes, List<Long> chromosomeKeys) throws IgniteException {

        Map<ComputeJob, ClusterNode> map = new HashMap<>();

        Affinity affinity = ignite.affinity(GAGridConstants.POPULATION_CACHE);

        Map<ClusterNode, Collection<Long>> nodeKeys = affinity.mapKeysToNodes(chromosomeKeys);

        for (Map.Entry<ClusterNode, Collection<Long>> entry : nodeKeys.entrySet()) {
            ClusterNode aNode = entry.getKey();
            map = setupCrossOver(aNode, (List<Long>) entry.getValue(), map);
        }
        return map;
    }

    /**
     * Helper method to help assign ComputeJobs to respective ClusterNodes
     * 
     * @param clusterNode
     * @param keys
     * @param map
     * @return Map<ComputeJob, ClusterNode>
     */

    private Map<ComputeJob, ClusterNode> setupCrossOver(ClusterNode clusterNode, List<Long> keys,
        Map<ComputeJob, ClusterNode> map) {
        // Calculate number of Jobs = keys / 2
        // as we desire pairs of Chromosomes to be swapped
        int numberOfJobs = keys.size() / 2;
        int k = 0;
        for (int i = 0; i < numberOfJobs; i++) {
            Long key1 = keys.get(k);
            Long key2 = keys.get(k + 1);

            CrossOverJob job = new CrossOverJob(key1, key2, this.config.getCrossOverRate());
            map.put(job, clusterNode);
            k = k + 2;
        }
        return map;
    }

    /**
     * We return TRUE if success, else Exection is thrown.
     * 
     * @param List<ComputeJobResult>
     * @return TRUE
     */
    public Boolean reduce(List<ComputeJobResult> list) throws IgniteException {
        // TODO Auto-generated method stub
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
