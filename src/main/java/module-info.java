module com.example.oopatmuiv3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.oopatmuiv3 to javafx.fxml;
    exports com.example.oopatmuiv3;
}