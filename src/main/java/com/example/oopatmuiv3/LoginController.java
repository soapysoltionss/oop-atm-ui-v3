package com.example.oopatmuiv3;

import java.io.IOException;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    ImageView eyesImageView;

    @FXML
    private Pane loginPane;


    @FXML
    private TextField userID;

    @FXML
    private PasswordField loginPIN;

    @FXML
    private Label loginLabel;

    @FXML
    private ToggleButton loginToggleButton;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField shownPassword;
    @FXML
    private TextField shownLoginPassword;



    private String bank_name = "kek";
    private Bank kekBank = new Bank(bank_name);


    // Strings which hold css elements to easily re-use in the application
    String errorStyle = "-fx-border-color: RED;";
    String successStyle = "-fx-border-color: #A9A9A9;";
    String textFillError = "-fx-text-fill: RED";

    private void launchDashboard(User c) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboardPage.fxml"));
        Parent root1 = fxmlLoader.load();
        DashboardController dController = fxmlLoader.getController();
        dController.currentUser = c;
        dController.setLabels();
        dController.showHomePane();
        Stage stage = new Stage();
        stage.setTitle(bank_name);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/kek.png"))));
        stage.setScene(new Scene(root1));
        stage.show();
    }
    private void closeWindow(){
        Stage stage = (Stage) userID.getScene().getWindow();
        stage.close();}

    @FXML
    public void userLogin() {
        try{
            User c = kekBank.userLogin(userID.getText(), loginPIN.getText());
            launchDashboard(c);
            closeWindow();
        }
        catch (InvalidLoginException e) {
            userID.setStyle(errorStyle);
            loginPIN.setStyle(errorStyle);
            loginLabel.setText(e.getMessage());
            loginLabel.setStyle(textFillError);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDemoAcc1(){
        userID.setText("5044891741");
        loginPIN.setText("1111");
    }
    public void setDemoAcc2(){
        userID.setText("8133389705");
        loginPIN.setText("6969");
    }




}