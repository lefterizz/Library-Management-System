package com.example.ece318_librarymanagementsys_uc1069790;

import com.example.ece318_librarymanagementsys_uc1069790.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

    @FXML private VBox bookDetailsPane;
    @FXML private Label detailTitle;
    @FXML private Label detailAuthor;
    @FXML private Label detailMainGenre;
    @FXML private Label detailSubGenre;
    @FXML private Label detailType;
    @FXML private Label detailPrice;
    @FXML private Label detailRating;
    @FXML private Label detailNumRated;
    @FXML private Label detailUrl;

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

        // Ensure the details pane starts hidden
        hideBookDetails();

        // Dummy data for now
        books.addAll(
                new Book("Clean Code", "Robert C. Martin", "Programming", "Software Engineering", "Paperback", 25.5, 4.8, 10500, "https://..."),
                new Book("Effective Java", "Joshua Bloch", "Programming", "Java", "Hardcover", 30.0, 4.7, 8500, "https://..."),
                new Book("Design Patterns", "GoF", "Programming", "Software Design", "Kindle", 22.0, 4.6, 7000, "https://...")
        );

        booksTable.setItems(books);

        // Show details when a book is selected
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                showBookDetails(newSel);
            } else {
                hideBookDetails();
            }
        });
    }

    private void showBookDetails(Book book) {
        detailTitle.setText(book.getTitle());
        detailAuthor.setText(book.getAuthor());
        detailMainGenre.setText(book.getMainGenre());
        detailSubGenre.setText(book.getSubGenre());
        detailType.setText(book.getType());
        detailPrice.setText(String.format("$%.2f", book.getPrice()));
        detailRating.setText(String.format("%.1f / 5", book.getRating()));
        detailNumRated.setText(String.valueOf(book.getNumRated()));
        detailUrl.setText(book.getUrl());

        bookDetailsPane.setManaged(true);
        bookDetailsPane.setVisible(true);
    }

    private void hideBookDetails() {
        bookDetailsPane.setManaged(false);
        bookDetailsPane.setVisible(false);
        detailTitle.setText("");
        detailAuthor.setText("");
        detailMainGenre.setText("");
        detailSubGenre.setText("");
        detailType.setText("");
        detailPrice.setText("");
        detailRating.setText("");
        detailNumRated.setText("");
        detailUrl.setText("");
    }

    @FXML
    private void handleCloseDetails() {
        booksTable.getSelectionModel().clearSelection();
        hideBookDetails();
    }
}
