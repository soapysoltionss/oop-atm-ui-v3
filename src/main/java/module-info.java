//<a target="_blank" href="https://icons8.com/icon/vEuuRDifMC1T/deposit">Deposit</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
module com.example.oopatmuiv3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;


    opens com.example.oopatmuiv3 to javafx.fxml;
    exports com.example.oopatmuiv3;
}