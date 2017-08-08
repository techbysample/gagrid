package com.nm.ignite.gagrid;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * 
 * Represents the discrete parts of a potential solution (ie: Chromosome)
 * 
 * Gene is a container for a POJO that developer will implement.
 * 
 * For the Movie Fitness example, the Movie object is the POJO contained within Gene.
 * 
 * NOTE: Gene resides in cache: 'geneCache'. This cache is replicated.
 *
 * @author turik.campbell
 *
 */
public class Gene {

    private static final AtomicLong ID_GEN = new AtomicLong();

    /** Id (indexed). */
    @QuerySqlField(index = true)
    private Long id;

    private Object value;

    public Gene(Object object) {
        id = ID_GEN.incrementAndGet();
        this.value = object;
    }

    public Long id() {
        return id;
    }

    public void setValue(Object object) {
        this.value = object;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Gene [id=" + id + ", value=" + value + "]";
    }

}
