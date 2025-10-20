package com.example.ece318_librarymanagementsys_uc1069790;

import com.example.ece318_librarymanagementsys_uc1069790.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab booksTab;

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

    private Tab bookDetailsTab;
    private Label detailTitleValue;
    private Label detailAuthorValue;
    private Label detailGenreValue;
    private Label detailSubGenreValue;
    private Label detailTypeValue;
    private Label detailPriceValue;
    private Label detailRatingValue;

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

        setupBookDetailsTab();

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
                hideBookDetailsTab();
            }
        });
    }

    private void setupBookDetailsTab() {
        bookDetailsTab = new Tab("Book Details");
        bookDetailsTab.setClosable(true);

        detailTitleValue = createDetailValueLabel();
        detailAuthorValue = createDetailValueLabel();
        detailGenreValue = createDetailValueLabel();
        detailSubGenreValue = createDetailValueLabel();
        detailTypeValue = createDetailValueLabel();
        detailPriceValue = createDetailValueLabel();
        detailRatingValue = createDetailValueLabel();

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);

        addDetailRow(content, 0, "Title:", detailTitleValue);
        addDetailRow(content, 1, "Author:", detailAuthorValue);
        addDetailRow(content, 2, "Main Genre:", detailGenreValue);
        addDetailRow(content, 3, "Sub-Genre:", detailSubGenreValue);
        addDetailRow(content, 4, "Type:", detailTypeValue);
        addDetailRow(content, 5, "Price:", detailPriceValue);
        addDetailRow(content, 6, "Rating:", detailRatingValue);

        VBox wrapper = new VBox(content);
        wrapper.setSpacing(10);
        VBox.setVgrow(content, Priority.ALWAYS);
        wrapper.setPadding(new javafx.geometry.Insets(20));
        bookDetailsTab.setContent(wrapper);

        bookDetailsTab.setOnClosed(event -> booksTable.getSelectionModel().clearSelection());
    }

    private Label createDetailValueLabel() {
        Label label = new Label();
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private void addDetailRow(GridPane grid, int rowIndex, String header, Label valueLabel) {
        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-weight: bold;");

        grid.add(headerLabel, 0, rowIndex);
        grid.add(valueLabel, 1, rowIndex);

        GridPane.setHgrow(valueLabel, Priority.ALWAYS);
    }

    private void showBookDetails(Book book) {
        if (!mainTabPane.getTabs().contains(bookDetailsTab)) {
            mainTabPane.getTabs().add(bookDetailsTab);
        }

        detailTitleValue.setText(book.getTitle());
        detailAuthorValue.setText(book.getAuthor());
        detailGenreValue.setText(book.getMainGenre());
        detailSubGenreValue.setText(book.getSubGenre());
        detailTypeValue.setText(book.getType());
        detailPriceValue.setText("$" + String.format("%.2f", book.getPrice()));
        detailRatingValue.setText(book.getRating() + " (" + book.getNumRated() + " ratings)");

        mainTabPane.getSelectionModel().select(bookDetailsTab);
    }

    private void hideBookDetailsTab() {
        if (mainTabPane.getTabs().contains(bookDetailsTab)) {
            mainTabPane.getTabs().remove(bookDetailsTab);
        }
        mainTabPane.getSelectionModel().select(booksTab);
    }
}
