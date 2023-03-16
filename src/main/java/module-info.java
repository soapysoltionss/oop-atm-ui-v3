module com.example.oopatmuiv3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;


    opens com.example.oopatmuiv3 to javafx.fxml;
    exports com.example.oopatmuiv3;
}