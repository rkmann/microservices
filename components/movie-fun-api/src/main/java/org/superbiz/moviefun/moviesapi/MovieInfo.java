package org.superbiz.moviefun.moviesapi;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class MovieInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String director;
    private String title;
    private int year;
    private String genre;
    private int rating;

    public MovieInfo() {
    }

    public MovieInfo(String title, String director, String genre, int rating, int year) {

        this.director = director;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieInfo)) return false;
        MovieInfo movieInfo = (MovieInfo) o;
        return id == movieInfo.id &&
                year == movieInfo.year &&
                rating == movieInfo.rating &&
                Objects.equals(director, movieInfo.director) &&
                Objects.equals(title, movieInfo.title) &&
                Objects.equals(genre, movieInfo.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, director, title, year, genre, rating);
    }
}
