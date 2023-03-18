package com.example.oopatmuiv3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    public void userLogin() {
    }

    public void showSignUpPane(ActionEvent actionEvent) {
    }

    public void showLoginPassword(ActionEvent actionEvent) {
    }

    public void showPassword(ActionEvent actionEvent) {
    }

    public void addUser(ActionEvent actionEvent) {
    }

    public void showLoginPane(ActionEvent actionEvent) {
    }
}