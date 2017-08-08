package com.nm.ignite.gagrid.samples.movie;

import java.util.List;

/**
 *
 *  POJO to model a movie.
 *  
 * @author turik.campbell
 *
 */

public class Movie {

    private String name;
    private List genre;
    private String rating;

    private double imdbRating;
    private String year;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getGenre() {
        return genre;
    }

    public void setGenre(List genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String toString() {
        return "Movie [name=" + name + ", genre=" + genre + ", rating=" + rating + ", imdbRating=" + imdbRating
            + ", year=" + year + "]";
    }

}
