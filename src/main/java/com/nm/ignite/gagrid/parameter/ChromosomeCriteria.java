package com.nm.ignite.gagrid.parameter;

import java.util.ArrayList;

import java.util.List;

/**
 * 
 * Responsible for describing the characteristics of an individual Chromosome. <br/>
 * <br/>
 * The characteristic will depend on the POJO you create to model a Gene. <br/>
 * <br/>
 * Example usage: <br/>
 * <br/>
 * in the MakeChangeGATest sample, we describe a chromsome with: <br/>
 * <br/>
 * QUARTERS,DIMES,NICKELS,PENNIES <br/>
 * <br/>
 * <br/>
 * <br/>
 * In this example, We use 'coinType' attribute from the 'Coin' class to describe the type of coin. <br/>
 * <br/>
 * The framework ensures the following to be true: <br/>
 * <br/>
 * Only 'Quarters' can appear in 0th index of chromosome. <br/>
 * Only 'Dimes' can appear in 1st index of chromosome. <br/>
 * Only 'Nickels' can appear in 2nd index of chromosome. <br/>
 * Only 'Pennies' can appear in 3nd index of chromosome. <br/>
 * <p>
 * 
 * // ChromsomeCriteria is container for a List<String> representing 'Name/Value' pairs in the form: Name=Value <br/>
 * ChromosomeCriteria chromosomeCritiera = new ChromosomeCriteria(); <br/>
 * 
 * List values = new ArrayList(); <br/>
 * values.add("coinType=QUARTER"); <br/>
 * values.add("coinType=DIME"); <br/>
 * values.add("coinType=NICKEL"); <br/>
 * values.add("coinType=PENNY"); <br/>
 * <br/>
 * chromosomeCritiera.setCriteria(values);<br/>
 * gaConfig.setChromosomeCritiera(chromosomeCritiera);<br/>
 * <br/>
 * <br/>
 * </p>
 * 
 * 
 * @author turik.campbell
 *
 */
public class ChromosomeCriteria {

    List<String> criteria = new ArrayList();

    /**
     * Retrieve criteria
     * 
     * @return List<String>
     */
    public List getCriteria() {
        return criteria;
    }

    /**
     * @param List<String>
     *            - Use format "name=value", ie: "coinType=QUARTER"
     */
    public void setCriteria(List criteria) {
        this.criteria = criteria;
    }

}
