package com.example.ece318_librarymanagementsys_uc1069790;

import com.example.ece318_librarymanagementsys_uc1069790.data.LibraryRepository;
import com.example.ece318_librarymanagementsys_uc1069790.model.Book;
import com.example.ece318_librarymanagementsys_uc1069790.model.Genre;
import com.example.ece318_librarymanagementsys_uc1069790.model.SubGenre;
import com.example.ece318_librarymanagementsys_uc1069790.util.SimplePdfExporter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab booksTab;

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colGenre;
    @FXML private TableColumn<Book, String> colSubGenre;
    @FXML private TableColumn<Book, Number> colPrice;
    @FXML private TableColumn<Book, Number> colRating;

    @FXML private TextField searchBooksField;
    @FXML private CheckComboBox<String> filterGenreBox;
    @FXML private ComboBox<String> filterSubGenreBox;
    @FXML private ChoiceBox<String> sortFieldChoice;
    @FXML private ToggleButton sortDirectionToggle;
    @FXML private Button clearBookFiltersButton;

    @FXML private VBox bookDetailsPane;
    @FXML private Label detailTitleValue;
    @FXML private Label detailAuthorValue;
    @FXML private Label detailGenreValue;
    @FXML private Label detailSubGenreValue;
    @FXML private Label detailTypeValue;
    @FXML private Label detailPriceValue;
    @FXML private Label detailRatingValue;
    @FXML private Label detailNumRatingsValue;
    @FXML private Hyperlink detailUrlValue;

    @FXML private VBox bookDetailsPane;
    @FXML private Label detailTitleValue;
    @FXML private Label detailAuthorValue;
    @FXML private Label detailGenreValue;
    @FXML private Label detailSubGenreValue;
    @FXML private Label detailTypeValue;
    @FXML private Label detailPriceValue;
    @FXML private Label detailRatingValue;

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

    private Tab bookDetailsTab;

    @FXML
    public void initialize() {
        try {
            repository = new LibraryRepository();
        } catch (IllegalStateException ex) {
            showError("Unable to load data", ex.getMessage());
            throw ex;
        }

        setupBooksTab();
        setupGenresTab();
        setupSubGenresTab();
        hideBookDetails();

        repository.getGenres().addListener((ListChangeListener<Genre>) change -> refreshGenreFilters());
        repository.getSubGenres().addListener((ListChangeListener<SubGenre>) change -> refreshSubGenreFilterOptions());
    }

        setupBookDetailsTab();

        // Ensure the details pane starts hidden
        hideBookDetails();

        // Dummy data for now
        books.addAll(
                new Book("Clean Code", "Robert C. Martin", "Programming", "Software Engineering", "Paperback", 25.5, 4.8, 10500, "https://..."),
                new Book("Effective Java", "Joshua Bloch", "Programming", "Java", "Hardcover", 30.0, 4.7, 8500, "https://..."),
                new Book("Design Patterns", "GoF", "Programming", "Software Design", "Kindle", 22.0, 4.6, 7000, "https://...")
        );

        booksTable.setItems(books);
        hideBookDetails();

        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                showBookDetails(newSel);
            } else {
                hideBookDetails();

            }
        });

        searchBooksField.textProperty().addListener((obs, old, text) -> updateBookFiltering());

        filterGenreBox.getItems().setAll(repository.getAllMainGenres());
        filterGenreBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            refreshSubGenreFilterOptions();
            updateBookFiltering();
        });

        filterSubGenreBox.valueProperty().addListener((obs, old, val) -> updateBookFiltering());
        filterSubGenreBox.setPromptText("All sub-genres");

        sortFieldChoice.setItems(FXCollections.observableArrayList("Rating", "Price"));
        sortFieldChoice.setValue("Rating");
        sortFieldChoice.valueProperty().addListener((obs, old, val) -> applyBookSorting());

        sortDirectionToggle.selectedProperty().addListener((obs, old, val) -> {
            sortDirectionToggle.setText(val ? "Descending" : "Ascending");
            applyBookSorting();
        });
        sortDirectionToggle.setSelected(true);

        clearBookFiltersButton.setOnAction(evt -> clearBookFilters());
        refreshSubGenreFilterOptions();
        applyBookSorting();
    }

    private void setupGenresTab() {
        colGenreName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colGenreSubCount.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSubGenreCount()));
        colGenreTotalBooks.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalBooks()));
        colGenreAvgRating.setCellValueFactory(data -> new SimpleDoubleProperty(round2(data.getValue().getAverageRating())));
        colGenreAvgPrice.setCellValueFactory(data -> new SimpleDoubleProperty(round2(data.getValue().getAveragePrice())));
        colGenreUrl.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrl()));

        filteredGenres = new FilteredList<>(repository.getGenres(), genre -> true);
        genresTable.setItems(filteredGenres);

        searchGenresField.textProperty().addListener((obs, old, text) -> {
            String lower = text == null ? "" : text.toLowerCase();
            filteredGenres.setPredicate(genre -> genre.getName().toLowerCase().contains(lower));
        });
    }

    private void setupSubGenresTab() {
        colSubGenreName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colParentGenre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMainGenre()));
        colSubGenreBooks.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBookCount()));
        colSubGenreAvgRating.setCellValueFactory(data -> new SimpleDoubleProperty(round2(data.getValue().getAverageRating())));
        colSubGenreAvgPrice.setCellValueFactory(data -> new SimpleDoubleProperty(round2(data.getValue().getAveragePrice())));
        colSubGenreUrl.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrl()));

        filteredSubGenres = new FilteredList<>(repository.getSubGenres(), sub -> true);
        subGenresTable.setItems(filteredSubGenres);

        searchSubGenresField.textProperty().addListener((obs, old, text) -> {
            String lower = text == null ? "" : text.toLowerCase();
            filteredSubGenres.setPredicate(sub -> sub.getName().toLowerCase().contains(lower)
                    || sub.getMainGenre().toLowerCase().contains(lower));
        });
    }

    private void updateBookFiltering() {
        String searchText = searchBooksField.getText() == null ? "" : searchBooksField.getText().toLowerCase();
        Set<String> selectedGenres = filterGenreBox.getCheckModel().getCheckedItems().stream().collect(Collectors.toSet());
        String selectedSubGenre = filterSubGenreBox.getValue();

        Predicate<Book> predicate = book -> {
            boolean matchesSearch = searchText.isBlank()
                    || book.getTitle().toLowerCase().contains(searchText)
                    || book.getAuthor().toLowerCase().contains(searchText);

            boolean matchesMainGenre = selectedGenres.isEmpty() || selectedGenres.contains(book.getMainGenre());
            boolean matchesSubGenre = selectedSubGenre == null || selectedSubGenre.isBlank()
                    || selectedSubGenre.equals("All sub-genres")
                    || book.getSubGenre().equals(selectedSubGenre);

            return matchesSearch && matchesMainGenre && matchesSubGenre;
        };

        filteredBooks.setPredicate(predicate);
    }

    private void applyBookSorting() {
        String field = sortFieldChoice.getValue();
        boolean descending = sortDirectionToggle.isSelected();

        Comparator<Book> comparator;
        if ("Rating".equals(field)) {
            comparator = Comparator.comparingDouble(Book::getRating)
                    .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else if ("Price".equals(field)) {
            comparator = Comparator.comparingDouble(Book::getPrice)
                    .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        }

        if (descending) {
            comparator = comparator.reversed();
        }

        sortedBooks.setComparator(comparator);
    }

    private void refreshGenreFilters() {
        List<String> checked = new ArrayList<>(filterGenreBox.getCheckModel().getCheckedItems());
        filterGenreBox.getItems().setAll(repository.getAllMainGenres());
        for (String item : checked) {
            if (filterGenreBox.getItems().contains(item)) {
                filterGenreBox.getCheckModel().check(item);
            }
        }
        refreshSubGenreFilterOptions();
    }

    private void refreshSubGenreFilterOptions() {
        Set<String> selectedGenres = filterGenreBox.getCheckModel().getCheckedItems().stream().collect(Collectors.toSet());
        List<String> subGenreOptions = repository.getSubGenres().stream()
                .filter(sub -> selectedGenres.isEmpty() || selectedGenres.contains(sub.getMainGenre()))
                .map(SubGenre::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        subGenreOptions.add(0, "All sub-genres");
        String current = filterSubGenreBox.getValue();
        filterSubGenreBox.getItems().setAll(subGenreOptions);
        if (current != null && filterSubGenreBox.getItems().contains(current)) {
            filterSubGenreBox.setValue(current);
        } else {
            filterSubGenreBox.setValue("All sub-genres");
        }
    }

    private void clearBookFilters() {
        searchBooksField.clear();
        filterGenreBox.getCheckModel().clearChecks();
        filterSubGenreBox.setValue("All sub-genres");
        sortFieldChoice.setValue("Rating");
        sortDirectionToggle.setSelected(true);
        updateBookFiltering();
        applyBookSorting();
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
        bookDetailsPane.setManaged(true);
        bookDetailsPane.setVisible(true);

        detailTitleValue.setText(book.getTitle());
        detailAuthorValue.setText(book.getAuthor());
        detailGenreValue.setText(book.getMainGenre());
        detailSubGenreValue.setText(book.getSubGenre());
        detailTypeValue.setText(book.getType());
        detailPriceValue.setText("$" + String.format("%.2f", book.getPrice()));
        detailRatingValue.setText(book.getRating() + " (" + book.getNumRated() + " ratings)");
    }

    private void hideBookDetails() {
        bookDetailsPane.setVisible(false);
        bookDetailsPane.setManaged(false);
        detailTitleValue.setText("-");
        detailAuthorValue.setText("-");
        detailGenreValue.setText("-");
        detailSubGenreValue.setText("-");
        detailTypeValue.setText("-");
        detailPriceValue.setText("-");
        detailRatingValue.setText("-");
        detailNumRatingsValue.setText("-");
        detailUrlValue.setText("No link available");
        detailUrlValue.setDisable(true);
    }

    @FXML
    private void onCloseDetails() {
        booksTable.getSelectionModel().clearSelection();
        hideBookDetails();
    }

    @FXML
    private void onBookUrlClicked(ActionEvent event) {
        if (selectedBook == null) {
            return;
        }
        String url = selectedBook.getUrl();
        if (url == null || url.isBlank()) {
            return;
        }
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (IOException | URISyntaxException ignored) {
            showWarning("Unable to open link", "The URL could not be opened on this system.");
        }
    }

    @FXML
    private void onAddBook() {
        Optional<Book> result = showBookDialog(null);
        result.ifPresent(book -> {
            repository.addBook(book);
            booksTable.getSelectionModel().select(book);
        });
    }

    @FXML
    private void onEditBook() {
        Book selection = booksTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showWarning("Select a book", "Please choose a book to edit from the table.");
            return;
        }
        Optional<Book> result = showBookDialog(selection);
        result.ifPresent(updated -> {
            repository.updateBook(updated);
            booksTable.getSelectionModel().select(updated);
        });
    }

    @FXML
    private void onDeleteBook() {
        Book selection = booksTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showWarning("Select a book", "Please choose a book to delete from the table.");
            return;
        }
        boolean confirmed = showConfirmation("Delete book", "Delete '" + selection.getTitle() + "'?");
        if (confirmed) {
            repository.deleteBook(selection);
            hideBookDetails();
        }
    }

    @FXML
    private void onExportBooks() {
        exportReport("Books Report", buildBookReportLines(sortedBooks), "books-report.pdf");
    }

    @FXML
    private void onAddGenre() {
        Optional<Genre> result = showGenreDialog(null);
        result.ifPresent(genre -> repository.addGenre(genre));
    }

    @FXML
    private void onEditGenre() {
        Genre selection = genresTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showWarning("Select a genre", "Choose a genre to update.");
            return;
        }
        Optional<Genre> result = showGenreDialog(selection);
        result.ifPresent(repository::updateGenre);
    }

    @FXML
    private void onDeleteGenre() {
        Genre selection = genresTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showWarning("Select a genre", "Choose a genre to delete.");
            return;
        }
        String message = "Deleting the genre will remove its sub-genres and books. Continue?";
        if (showConfirmation("Delete genre", message)) {
            repository.deleteGenre(selection);
        }
    }

    @FXML
    private void onExportGenres() {
        exportReport("Genres Report", buildGenreReportLines(filteredGenres), "genres-report.pdf");
    }

    @FXML
    private void onAddSubGenre() {
        Optional<SubGenre> result = showSubGenreDialog(null);
        result.ifPresent(repository::addSubGenre);
    }

    @FXML
    private void onEditSubGenre() {
        SubGenre selection = subGenresTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showWarning("Select a sub-genre", "Choose a sub-genre to update.");
            return;
        }
        Optional<SubGenre> result = showSubGenreDialog(selection);
        result.ifPresent(repository::updateSubGenre);
    }

    @FXML
    private void onDeleteSubGenre() {
        SubGenre selection = subGenresTable.getSelectionModel().getSelectedItem();
        if (selection == null) {
            showWarning("Select a sub-genre", "Choose a sub-genre to delete.");
            return;
        }
        String message = "Deleting the sub-genre will remove associated books. Continue?";
        if (showConfirmation("Delete sub-genre", message)) {
            repository.deleteSubGenre(selection);
        }
    }

    @FXML
    private void onExportSubGenres() {
        exportReport("Sub-Genres Report", buildSubGenreReportLines(filteredSubGenres), "sub-genres-report.pdf");
    }

    private Optional<Book> showBookDialog(Book existing) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Book" : "Edit Book");

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField titleField = new TextField();
        TextField authorField = new TextField();
        ComboBox<String> mainGenreField = new ComboBox<>(FXCollections.observableArrayList(repository.getAllMainGenres()));
        mainGenreField.setEditable(true);
        ComboBox<String> subGenreField = new ComboBox<>(FXCollections.observableArrayList(repository.getAllSubGenres()));
        subGenreField.setEditable(true);
        TextField typeField = new TextField();
        TextField priceField = new TextField();
        TextField ratingField = new TextField();
        TextField numRatedField = new TextField();
        TextField urlField = new TextField();

        grid.addRow(0, new Label("Title"), titleField);
        grid.addRow(1, new Label("Author"), authorField);
        grid.addRow(2, new Label("Main Genre"), mainGenreField);
        grid.addRow(3, new Label("Sub-genre"), subGenreField);
        grid.addRow(4, new Label("Type"), typeField);
        grid.addRow(5, new Label("Price"), priceField);
        grid.addRow(6, new Label("Rating"), ratingField);
        grid.addRow(7, new Label("# Ratings"), numRatedField);
        grid.addRow(8, new Label("URL"), urlField);

        GridPane.setHgrow(titleField, Priority.ALWAYS);
        GridPane.setHgrow(authorField, Priority.ALWAYS);
        GridPane.setHgrow(mainGenreField, Priority.ALWAYS);
        GridPane.setHgrow(subGenreField, Priority.ALWAYS);
        GridPane.setHgrow(typeField, Priority.ALWAYS);
        GridPane.setHgrow(priceField, Priority.ALWAYS);
        GridPane.setHgrow(ratingField, Priority.ALWAYS);
        GridPane.setHgrow(numRatedField, Priority.ALWAYS);
        GridPane.setHgrow(urlField, Priority.ALWAYS);

        if (existing != null) {
            titleField.setText(existing.getTitle());
            authorField.setText(existing.getAuthor());
            mainGenreField.setValue(existing.getMainGenre());
            subGenreField.setValue(existing.getSubGenre());
            typeField.setText(existing.getType());
            priceField.setText(String.valueOf(existing.getPrice()));
            ratingField.setText(String.valueOf(existing.getRating()));
            numRatedField.setText(String.valueOf(existing.getNumRated()));
            urlField.setText(existing.getUrl());
        }

        pane.setContent(grid);

        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        titleField.getText().isBlank()
                                || authorField.getText().isBlank()
                                || mainGenreField.getEditor().getText().isBlank()
                                || subGenreField.getEditor().getText().isBlank()
                                || typeField.getText().isBlank()
                                || priceField.getText().isBlank()
                                || ratingField.getText().isBlank()
                                || numRatedField.getText().isBlank(),
                titleField.textProperty(),
                authorField.textProperty(),
                mainGenreField.getEditor().textProperty(),
                subGenreField.getEditor().textProperty(),
                typeField.textProperty(),
                priceField.textProperty(),
                ratingField.textProperty(),
                numRatedField.textProperty()));

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                double rating = Double.parseDouble(ratingField.getText().trim());
                int numRated = Integer.parseInt(numRatedField.getText().trim());
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String mainGenre = mainGenreField.getEditor().getText().trim();
                String subGenre = subGenreField.getEditor().getText().trim();
                String type = typeField.getText().trim();
                String url = urlField.getText().trim();

                if (existing != null) {
                    return existing.withUpdates(title, author, mainGenre, subGenre, type, price, rating, numRated, url);
                }
                return new Book(title, author, mainGenre, subGenre, type, price, rating, numRated, url);
            } catch (NumberFormatException ex) {
                showWarning("Invalid values", "Price, rating, and number of ratings must be numeric.");
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private Optional<Genre> showGenreDialog(Genre existing) {
        Dialog<Genre> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Genre" : "Edit Genre");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField();
        TextField subCountField = new TextField();
        TextField urlField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Sub-genres"), subCountField);
        grid.addRow(2, new Label("URL"), urlField);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(subCountField, Priority.ALWAYS);
        GridPane.setHgrow(urlField, Priority.ALWAYS);

        if (existing != null) {
            nameField.setText(existing.getName());
            subCountField.setText(String.valueOf(existing.getSubGenreCount()));
            urlField.setText(existing.getUrl());
        }

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(Bindings.createBooleanBinding(() -> nameField.getText().isBlank(),
                nameField.textProperty()));

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            try {
                int subCount = Integer.parseInt(subCountField.getText().trim().isEmpty() ? "0" : subCountField.getText().trim());
                String url = urlField.getText().trim();
                String name = nameField.getText().trim();

                if (existing != null) {
                    return existing.withUpdates(name, subCount, url);
                }
                return new Genre(name, subCount, url);
            } catch (NumberFormatException ex) {
                showWarning("Invalid value", "Sub-genre count must be numeric.");
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private Optional<SubGenre> showSubGenreDialog(SubGenre existing) {
        Dialog<SubGenre> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Sub-genre" : "Edit Sub-genre");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField();
        ComboBox<String> mainGenreField = new ComboBox<>(FXCollections.observableArrayList(repository.getAllMainGenres()));
        mainGenreField.setEditable(true);
        TextField booksField = new TextField();
        TextField urlField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Main Genre"), mainGenreField);
        grid.addRow(2, new Label("# Books"), booksField);
        grid.addRow(3, new Label("URL"), urlField);
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(mainGenreField, Priority.ALWAYS);
        GridPane.setHgrow(booksField, Priority.ALWAYS);
        GridPane.setHgrow(urlField, Priority.ALWAYS);

        if (existing != null) {
            nameField.setText(existing.getName());
            mainGenreField.setValue(existing.getMainGenre());
            booksField.setText(String.valueOf(existing.getBookCount()));
            urlField.setText(existing.getUrl());
        }

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        nameField.getText().isBlank() || mainGenreField.getEditor().getText().isBlank(),
                nameField.textProperty(),
                mainGenreField.getEditor().textProperty()));

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            try {
                int bookCount = Integer.parseInt(booksField.getText().trim().isEmpty() ? "0" : booksField.getText().trim());
                String name = nameField.getText().trim();
                String mainGenre = mainGenreField.getEditor().getText().trim();
                String url = urlField.getText().trim();

                if (existing != null) {
                    return existing.withUpdates(name, mainGenre, bookCount, url);
                }
                return new SubGenre(name, mainGenre, bookCount, url);
            } catch (NumberFormatException ex) {
                showWarning("Invalid value", "Number of books must be numeric.");
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private List<String> buildBookReportLines(List<? extends Book> books) {
        return books.stream()
                .map(book -> String.format("%s by %s | %s > %s | %s | %s | %.2f★ (%d ratings)",
                        book.getTitle(), book.getAuthor(), book.getMainGenre(), book.getSubGenre(),
                        book.getType(), formatPrice(book.getPrice()), book.getRating(), book.getNumRated()))
                .collect(Collectors.toList());
    }

    private List<String> buildGenreReportLines(List<? extends Genre> genres) {
        return genres.stream()
                .map(genre -> String.format("%s | Sub-genres: %d | Books: %d | Avg Rating: %.2f | Avg Price: %s",
                        genre.getName(), genre.getSubGenreCount(), genre.getTotalBooks(),
                        round2(genre.getAverageRating()), formatPrice(genre.getAveragePrice())))
                .collect(Collectors.toList());
    }

    private List<String> buildSubGenreReportLines(List<? extends SubGenre> subGenres) {
        return subGenres.stream()
                .map(sub -> String.format("%s (%s) | Books: %d | Avg Rating: %.2f | Avg Price: %s",
                        sub.getName(), sub.getMainGenre(), sub.getBookCount(),
                        round2(sub.getAverageRating()), formatPrice(sub.getAveragePrice())))
                .collect(Collectors.toList());
    }

    private void exportReport(String title, List<String> lines, String suggestedFileName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        chooser.setInitialFileName(suggestedFileName);
        Window owner = booksTable.getScene() != null ? booksTable.getScene().getWindow() : null;
        File file = chooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }

        try {
            SimplePdfExporter.export(title, lines, Path.of(file.getAbsolutePath()));
            showInfo("Report exported", "Saved report to " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Export failed", "Unable to save report: " + ex.getMessage());
        }
    }

    private String formatPrice(double price) {
        return PRICE_FORMAT.format(price);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
