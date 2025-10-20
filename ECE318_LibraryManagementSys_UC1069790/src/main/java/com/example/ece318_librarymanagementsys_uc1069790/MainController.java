package com.example.ece318_librarymanagementsys_uc1069790;

import com.example.ece318_librarymanagementsys_uc1069790.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colGenre;
    @FXML private TableColumn<Book, String> colSubGenre;
    @FXML private TableColumn<Book, Double> colPrice;
    @FXML private TableColumn<Book, Double> colRating;

    @FXML private TextField searchBooksField;
    @FXML private ComboBox<String> filterGenreBox;
    @FXML private ComboBox<String> filterSubGenreBox;

    private final ObservableList<Book> books = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("GUI loaded successfully!");

        // Column mapping
        colTitle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        colAuthor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        colGenre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMainGenre()));
        colSubGenre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubGenre()));
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrice()));
        colRating.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRating()));

        // Auto-resize columns
        booksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Dummy data for now
        books.addAll(
                new Book("Clean Code", "Robert C. Martin", "Programming", "Software Engineering", "Paperback", 25.5, 4.8, 10500, "https://..."),
                new Book("Effective Java", "Joshua Bloch", "Programming", "Java", "Hardcover", 30.0, 4.7, 8500, "https://..."),
                new Book("Design Patterns", "GoF", "Programming", "Software Design", "Kindle", 22.0, 4.6, 7000, "https://...")
        );

        booksTable.setItems(books);

        // Show details when a book is selected
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) showBookDetails(newSel);
        });
    }

    private void showBookDetails(Book book) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Details");
        alert.setHeaderText(book.getTitle());
        alert.setContentText(
                "Author: " + book.getAuthor() + "\n" +
                        "Main Genre: " + book.getMainGenre() + "\n" +
                        "Sub-Genre: " + book.getSubGenre() + "\n" +
                        "Type: " + book.getType() + "\n" +
                        "Price: $" + book.getPrice() + "\n" +
                        "Rating: " + book.getRating() + " (" + book.getNumRated() + " ratings)"
        );
        alert.showAndWait();
    }
}
