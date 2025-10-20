package com.example.ece318_librarymanagementsys_uc1069790.model;

import java.util.Objects;
import java.util.UUID;

public class Genre {
    private final UUID id;
    private final String name;
    private int subGenreCount;
    private String url;
    private int totalBooks;
    private double averageRating;
    private double averagePrice;

    public Genre(String name, int subGenreCount, String url) {
        this(UUID.randomUUID(), name, subGenreCount, url);
    }

    public Genre(UUID id, String name, int subGenreCount, String url) {
        this.id = id;
        this.name = name;
        this.subGenreCount = subGenreCount;
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSubGenreCount() {
        return subGenreCount;
    }

    public void setSubGenreCount(int subGenreCount) {
        this.subGenreCount = subGenreCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Genre withUpdates(String name, int subGenreCount, String url) {
        Genre updated = new Genre(id, name, subGenreCount, url);
        updated.setTotalBooks(totalBooks);
        updated.setAverageRating(averageRating);
        updated.setAveragePrice(averagePrice);
        return updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre genre)) return false;
        return id.equals(genre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
