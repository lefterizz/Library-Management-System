module com.example.ece318_librarymanagementsys_uc1069790 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens com.example.ece318_librarymanagementsys_uc1069790 to javafx.fxml;
    exports com.example.ece318_librarymanagementsys_uc1069790;
}