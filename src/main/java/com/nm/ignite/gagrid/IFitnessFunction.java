package com.nm.ignite.gagrid;

import java.util.List;

/**
 * 
 * Fitness function are used to determine how optimal a particular solution is relative to other solutions. The
 * evaluate() method should be implemented for this interface. The fitness function is provided list of Genes. The
 * evaluate method should return a positive double value that reflects fitness score.
 * 
 * @author turik.campbell
 *
 */
public interface IFitnessFunction {

    /**
     * 
     * @param genes
     *            - genes within an individual Chromosome
     * @return double
     */
    public double evaluate(List<Gene> genes);
}
