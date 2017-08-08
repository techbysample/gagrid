package com.nm.ignite.gagrid.parameter;

/**
 * 
 * 
 * 
 * @author turik.campbell
 *
 */
public interface GAGridConstants {

    public static final String POPULATION_CACHE = "populationCache";
    public static final String GENE_CACHE = "geneCache";

    public static enum FITNESS_EVALUATER_TYPE {
                                               HIGHEST_FITNESS_VALUE_IS_FITTER, LOWEST_FITNESS_VALUE_IS_FITTER
    };

    public static enum SELECTION_METHOD {
                                         SELECTON_METHOD_ELETISM, SELECTION_METHOD_TRUNCATION
    };
}
