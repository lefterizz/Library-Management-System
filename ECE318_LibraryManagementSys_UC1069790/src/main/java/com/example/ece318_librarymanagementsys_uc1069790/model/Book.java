package com.example.ece318_librarymanagementsys_uc1069790.model;

import java.util.Objects;
import java.util.UUID;

public class Book {
    private final UUID id;
    private final String title;
    private final String author;
    private final String mainGenre;
    private final String subGenre;
    private final String type;
    private final double price;
    private final double rating;
    private final int numRated;
    private final String url;

    public Book(String title, String author, String mainGenre, String subGenre,
                String type, double price, double rating, int numRated, String url) {
        this(UUID.randomUUID(), title, author, mainGenre, subGenre, type, price, rating, numRated, url);
    }

    public Book(UUID id, String title, String author, String mainGenre, String subGenre,
                String type, double price, double rating, int numRated, String url) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.mainGenre = mainGenre;
        this.subGenre = subGenre;
        this.type = type;
        this.price = price;
        this.rating = rating;
        this.numRated = numRated;
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getMainGenre() {
        return mainGenre;
    }

    public String getSubGenre() {
        return subGenre;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public int getNumRated() {
        return numRated;
    }

    public String getUrl() {
        return url;
    }

    public Book withUpdates(String title, String author, String mainGenre, String subGenre,
                             String type, double price, double rating, int numRated, String url) {
        return new Book(id, title, author, mainGenre, subGenre, type, price, rating, numRated, url);
    }

    @Override
    public String toString() {
        return title + " by " + author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
