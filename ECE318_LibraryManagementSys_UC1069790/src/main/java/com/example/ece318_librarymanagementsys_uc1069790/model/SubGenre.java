package com.example.ece318_librarymanagementsys_uc1069790.model;

import java.util.Objects;
import java.util.UUID;

public class SubGenre {
    private final UUID id;
    private final String name;
    private String mainGenre;
    private int bookCount;
    private String url;
    private double averageRating;
    private double averagePrice;

    public SubGenre(String name, String mainGenre, int bookCount, String url) {
        this(UUID.randomUUID(), name, mainGenre, bookCount, url);
    }

    public SubGenre(UUID id, String name, String mainGenre, int bookCount, String url) {
        this.id = id;
        this.name = name;
        this.mainGenre = mainGenre;
        this.bookCount = bookCount;
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMainGenre() {
        return mainGenre;
    }

    public void setMainGenre(String mainGenre) {
        this.mainGenre = mainGenre;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public SubGenre withUpdates(String name, String mainGenre, int bookCount, String url) {
        SubGenre updated = new SubGenre(id, name, mainGenre, bookCount, url);
        updated.setAverageRating(averageRating);
        updated.setAveragePrice(averagePrice);
        return updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubGenre that)) return false;
        return id.equals(that.id);
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
