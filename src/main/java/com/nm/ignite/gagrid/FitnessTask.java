package com.nm.ignite.gagrid;

import java.util.ArrayList;
import java.util.Collections;
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
 * Responsible for fitness operation
 * 
 * 
 * @author turik.campbell
 *
 */
public class FitnessTask extends ComputeTaskAdapter<List<Long>, Boolean> {

    @IgniteInstanceResource
    private Ignite ignite = null;

    private GAConfiguration config = null;

    /**
     * 
     * @param GAConfiguration config
     */
    public FitnessTask(GAConfiguration config) {
        this.config = config;
    }

    /**
     * @param List<ClusterNode> nodes
     * @param List<Long> chromosomeKeys
     * 
     * @return Map<ComputeJob, ClusterNode>
     */
    public Map map(List<ClusterNode> nodes, List<Long> chromosomeKeys) throws IgniteException {

        Map<ComputeJob, ClusterNode> map = new HashMap<>();

        Affinity affinity = ignite.affinity(GAGridConstants.POPULATION_CACHE);

        for (Long key : chromosomeKeys) {

            FitnessJob ajob = new FitnessJob(key, this.config.getFitnessFunction());

            ClusterNode primary = affinity.mapKeyToNode(key);

            map.put(ajob, primary);
        }
        return map;
    }

   /**
    *  @param List<ComputeJobResult>
    *  @return Boolean
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
