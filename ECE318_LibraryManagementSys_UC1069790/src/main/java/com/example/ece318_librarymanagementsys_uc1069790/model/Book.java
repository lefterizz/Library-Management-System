package com.example.ece318_librarymanagementsys_uc1069790.model;

public class Book {
    private String title;
    private String author;
    private String mainGenre;
    private String subGenre;
    private String type;
    private double price;
    private double rating;
    private int numRated;
    private String url;

    public Book(String title, String author, String mainGenre, String subGenre,
                String type, double price, double rating, int numRated, String url) {
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

    // Getters only (add setters later if you plan to edit)
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getMainGenre() { return mainGenre; }
    public String getSubGenre() { return subGenre; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public int getNumRated() { return numRated; }
    public String getUrl() { return url; }

    @Override
    public String toString() {
        return title + " by " + author;
    }
}
