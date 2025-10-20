package com.example.ece318_librarymanagementsys_uc1069790.data;

import com.example.ece318_librarymanagementsys_uc1069790.model.Book;
import com.example.ece318_librarymanagementsys_uc1069790.model.Genre;
import com.example.ece318_librarymanagementsys_uc1069790.model.SubGenre;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryRepository {

    private static final String BOOKS_RESOURCE = "/data/Books_df.csv";
    private static final String GENRES_RESOURCE = "/data/Genre_df.csv";
    private static final String SUB_GENRES_RESOURCE = "/data/Sub_Genre_df.csv";

    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private final ObservableList<Genre> genres = FXCollections.observableArrayList();
    private final ObservableList<SubGenre> subGenres = FXCollections.observableArrayList();

    public LibraryRepository() {
        try {
            loadGenres();
            loadSubGenres();
            loadBooks();
            recomputeAggregates();
            attachListeners();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load library data", ex);
        }
    }

    private void attachListeners() {
        books.addListener((ListChangeListener<Book>) change -> recomputeAggregates());
        subGenres.addListener((ListChangeListener<SubGenre>) change -> recomputeAggregates());
    }

    private void loadBooks() throws IOException {
        try (BufferedReader reader = openResource(BOOKS_RESOURCE)) {
            // Skip header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> tokens = parseCsvLine(line);
                if (tokens.size() < 10) {
                    continue;
                }
                String title = tokens.get(1);
                String author = tokens.get(2);
                String mainGenre = tokens.get(3);
                String subGenre = tokens.get(4);
                String type = tokens.get(5);
                double price = parseCurrency(tokens.get(6));
                double rating = parseDouble(tokens.get(7));
                int numRated = parseInt(tokens.get(8));
                String url = tokens.get(9);
                books.add(new Book(title, author, mainGenre, subGenre, type, price, rating, numRated, url));
            }
        }
    }

    private void loadGenres() throws IOException {
        try (BufferedReader reader = openResource(GENRES_RESOURCE)) {
            // header
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> tokens = parseCsvLine(line);
                if (tokens.size() < 3) {
                    continue;
                }
                String name = tokens.get(0);
                int subGenreCount = parseInt(tokens.get(1));
                String url = tokens.get(2);
                genres.add(new Genre(name, subGenreCount, url));
            }
        }
    }

    private void loadSubGenres() throws IOException {
        try (BufferedReader reader = openResource(SUB_GENRES_RESOURCE)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> tokens = parseCsvLine(line);
                if (tokens.size() < 4) {
                    continue;
                }
                String name = tokens.get(0);
                String mainGenre = tokens.get(1);
                int bookCount = parseInt(tokens.get(2));
                String url = tokens.get(3);
                subGenres.add(new SubGenre(name, mainGenre, bookCount, url));
            }
        }
    }

    private BufferedReader openResource(String path) throws IOException {
        InputStream inputStream = LibraryRepository.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + path);
        }
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private double parseCurrency(String raw) {
        String normalized = raw.replaceAll("[^0-9.]", "");
        if (normalized.isEmpty()) {
            return 0.0;
        }
        return parseDouble(normalized);
    }

    private double parseDouble(String raw) {
        try {
            return Double.parseDouble(raw.replaceAll("[^0-9.]+", ""));
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private int parseInt(String raw) {
        try {
            double value = Double.parseDouble(raw.replaceAll("[^0-9.]+", ""));
            return (int) Math.round(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void recomputeAggregates() {
        Map<String, List<Book>> booksByGenre = books.stream()
                .collect(Collectors.groupingBy(Book::getMainGenre));
        Map<String, List<Book>> booksBySubGenre = books.stream()
                .collect(Collectors.groupingBy(Book::getSubGenre));

        for (Genre genre : genres) {
            List<Book> genreBooks = booksByGenre.getOrDefault(genre.getName(), Collections.emptyList());
            genre.setTotalBooks(genreBooks.size());
            genre.setAverageRating(calculateAverage(genreBooks, Book::getRating));
            genre.setAveragePrice(calculateAverage(genreBooks, Book::getPrice));
            genre.setSubGenreCount((int) subGenres.stream()
                    .filter(sub -> sub.getMainGenre().equals(genre.getName()))
                    .count());
        }

        for (SubGenre subGenre : subGenres) {
            List<Book> subGenreBooks = booksBySubGenre.getOrDefault(subGenre.getName(), Collections.emptyList());
            subGenre.setBookCount(subGenreBooks.size());
            subGenre.setAverageRating(calculateAverage(subGenreBooks, Book::getRating));
            subGenre.setAveragePrice(calculateAverage(subGenreBooks, Book::getPrice));
        }
    }

    private double calculateAverage(List<Book> books, java.util.function.ToDoubleFunction<Book> extractor) {
        if (books.isEmpty()) {
            return 0.0;
        }
        return books.stream().mapToDouble(extractor).average().orElse(0.0);
    }

    public ObservableList<Book> getBooks() {
        return books;
    }

    public ObservableList<Genre> getGenres() {
        return genres;
    }

    public ObservableList<SubGenre> getSubGenres() {
        return subGenres;
    }

    public void addBook(Book book) {
        Book normalized = normalizeBookGenres(book);
        ensureGenreExists(normalized.getMainGenre());
        ensureSubGenreExists(normalized.getSubGenre(), normalized.getMainGenre());
        books.add(normalized);
    }

    public void updateBook(Book updated) {
        Book normalized = normalizeBookGenres(updated);
        ensureGenreExists(normalized.getMainGenre());
        ensureSubGenreExists(normalized.getSubGenre(), normalized.getMainGenre());
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(normalized.getId())) {
                books.set(i, normalized);
                return;
            }
        }
    }

    public void deleteBook(Book book) {
        books.removeIf(existing -> existing.getId().equals(book.getId()));
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
        recomputeAggregates();
    }

    public void updateGenre(Genre updated) {
        for (int i = 0; i < genres.size(); i++) {
            Genre current = genres.get(i);
            if (current.getId().equals(updated.getId())) {
                String previousName = current.getName();
                genres.set(i, updated);
                if (!previousName.equals(updated.getName())) {
                    updateBooksForGenreRename(previousName, updated.getName());
                    updateSubGenresForGenreRename(previousName, updated.getName());
                }
                recomputeAggregates();
                return;
            }
        }
    }

    public void deleteGenre(Genre genre) {
        books.removeIf(book -> book.getMainGenre().equalsIgnoreCase(genre.getName()));
        subGenres.removeIf(sub -> sub.getMainGenre().equalsIgnoreCase(genre.getName()));
        genres.removeIf(existing -> existing.getId().equals(genre.getId()));
        recomputeAggregates();
    }

    public void addSubGenre(SubGenre subGenre) {
        subGenres.add(subGenre);
        recomputeAggregates();
    }

    public void updateSubGenre(SubGenre updated) {
        for (int i = 0; i < subGenres.size(); i++) {
            SubGenre current = subGenres.get(i);
            if (current.getId().equals(updated.getId())) {
                String previousName = current.getName();
                String previousMain = current.getMainGenre();
                subGenres.set(i, updated);
                updateBooksForSubGenreChange(previousName, previousMain, updated);
                recomputeAggregates();
                return;
            }
        }
    }

    public void deleteSubGenre(SubGenre subGenre) {
        books.removeIf(book -> book.getSubGenre().equalsIgnoreCase(subGenre.getName()));
        subGenres.removeIf(existing -> existing.getId().equals(subGenre.getId()));
        recomputeAggregates();
    }

    public Set<String> getAllMainGenres() {
        return genres.stream().map(Genre::getName).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> getAllSubGenres() {
        return subGenres.stream().map(SubGenre::getName).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Book normalizeBookGenres(Book book) {
        String mainGenre = canonicalGenreName(book.getMainGenre());
        String subGenre = canonicalSubGenreName(book.getSubGenre(), mainGenre);
        if (!mainGenre.equals(book.getMainGenre()) || !subGenre.equals(book.getSubGenre())) {
            return book.withUpdates(book.getTitle(), book.getAuthor(), mainGenre, subGenre, book.getType(),
                    book.getPrice(), book.getRating(), book.getNumRated(), book.getUrl());
        }
        return book;
    }

    private String canonicalGenreName(String name) {
        return genres.stream()
                .filter(genre -> genre.getName().equalsIgnoreCase(name))
                .map(Genre::getName)
                .findFirst()
                .orElse(name);
    }

    private String canonicalSubGenreName(String name, String mainGenre) {
        return subGenres.stream()
                .filter(sub -> sub.getName().equalsIgnoreCase(name) && sub.getMainGenre().equalsIgnoreCase(mainGenre))
                .map(SubGenre::getName)
                .findFirst()
                .orElse(name);
    }

    private void ensureGenreExists(String name) {
        boolean exists = genres.stream().anyMatch(genre -> genre.getName().equalsIgnoreCase(name));
        if (!exists) {
            genres.add(new Genre(name, 0, ""));
        }
    }

    private void ensureSubGenreExists(String name, String mainGenre) {
        if (name == null || name.isBlank()) {
            return;
        }
        ensureGenreExists(mainGenre);
        boolean exists = subGenres.stream()
                .anyMatch(sub -> sub.getName().equalsIgnoreCase(name) && sub.getMainGenre().equalsIgnoreCase(mainGenre));
        if (!exists) {
            subGenres.add(new SubGenre(name, mainGenre, 0, ""));
        }
    }

    private void updateBooksForGenreRename(String previousName, String newName) {
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if (book.getMainGenre().equals(previousName)) {
                books.set(i, book.withUpdates(book.getTitle(), book.getAuthor(), newName,
                        book.getSubGenre(), book.getType(), book.getPrice(), book.getRating(), book.getNumRated(), book.getUrl()));
            }
        }
    }

    private void updateSubGenresForGenreRename(String previousName, String newName) {
        for (SubGenre subGenre : subGenres) {
            if (subGenre.getMainGenre().equals(previousName)) {
                subGenre.setMainGenre(newName);
            }
        }
    }

    private void updateBooksForSubGenreChange(String previousName, String previousMainGenre, SubGenre updated) {
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if (book.getSubGenre().equals(previousName)) {
                books.set(i, book.withUpdates(book.getTitle(), book.getAuthor(), updated.getMainGenre(),
                        updated.getName(), book.getType(), book.getPrice(), book.getRating(), book.getNumRated(), book.getUrl()));
            } else if (book.getMainGenre().equals(previousMainGenre) && updated.getMainGenre().equals(previousMainGenre)) {
                // ensure consistency if only name changed but book sub-genre differs
                books.set(i, book.withUpdates(book.getTitle(), book.getAuthor(), updated.getMainGenre(),
                        book.getSubGenre(), book.getType(), book.getPrice(), book.getRating(), book.getNumRated(), book.getUrl()));
            }
        }
    }
}
