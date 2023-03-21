package com.example.oopatmuiv3;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class DashboardController {

    @FXML
    private Label accNumber;
    @FXML
    private Label usrID;

    @FXML
    private Label balance;

    @FXML
    private Button confirmDepositButton;

    @FXML
    private Button confirmTransferButton;

    @FXML
    private Button confirmWithdrawButton;

    @FXML
    private TextField depositAmountTextField;

    @FXML
    private Button depositButton;

    @FXML
    private Label depositConfirmationText;

    @FXML
    private Pane depositPane;





    @FXML
    private Button homeButton;

    @FXML
    private Pane homePane;

    @FXML
    private Label name;

    @FXML
    private TextField recieverTextField;

    @FXML
    private Button resetButton;

    @FXML
    private TextField resultAmount;

    @FXML
    private TextField transferAmountTextField;

    @FXML
    private Button transferButton;

    @FXML
    private Button swapButton;

    @FXML
    private Label transferConfirmationText;

    @FXML
    private Pane transferPane;

    @FXML
    private TextField withdrawAmountTextField;

    @FXML
    private Button withdrawButton;

    @FXML
    private Label withdrawConfirmationText;

    @FXML
    private Pane withdrawPane;


    @FXML
    private ListView<String> transactionLs;
    @FXML
    private ListView<String> accLsOverview;
    @FXML
    private ListView<String> accLsDeposit;
    @FXML
    private ListView<String> accLsWithdraw;
    @FXML
    private ListView<String> accLsTransfer;


    @FXML
    private TextField transferToLocalAcc; //NEED TO SET IN FXML PAGE
    @FXML
    private  TextField transferLocalMemo;
    //NEED TO SET FOR NON-LOCAL TRANSFERS TOO


    String errorStyle = "-fx-text-fill: RED;";
    String successStyle = "-fx-text-fill: GREEN;";

    String bank_name = "kek";
    protected User currentUser;
    private Integer selectedAcc = 0;
    DecimalFormat df = new DecimalFormat("0.00");



    // tbd many accounts to 1 user to do pane
    public void setLabels() {
        name.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        usrID.setText(currentUser.getUUID());
        accNumber.setText(currentUser.getAccount(selectedAcc).getUUID());
        balance.setText(String.format("$" + "%.2f",currentUser.getAccount(selectedAcc).getBalance()));
        ObservableList<String> transactions = FXCollections.observableArrayList(currentUser.getAccount(selectedAcc).getTransactionHistory());
        transactionLs.setItems(transactions);
    }
    public void showTransferPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(true);
    }

    public void confirmDeposit() {
        try{
            double amount = Double.parseDouble(depositAmountTextField.getText());
            currentUser.getAccount(selectedAcc).deposit(amount);
            depositConfirmationText.setText("Successfully Deposited: "+ " $" + df.format(amount)+"\nCurrent Bal: "+ "$" + df.format(currentUser.getAccount(selectedAcc).getBalance()));

            /*
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Confirm Deposit");
            a.setContentText("Successfully Deposited: "+ " $" + df.format(amount)+"\nCurrent Bal: "+ "$" + df.format(currentUser.getAccount(selectedAcc).getBalance()));
            a.showAndWait();
            */
            //showHomePane();
        }
        //set our own exception cases & set alert to the e.getMessage()
        catch(NumberFormatException e){
            depositConfirmationText.setText("Please Enter a Numeric Value");
            depositConfirmationText.setStyle(errorStyle);
            depositAmountTextField.setText("");
        } catch (InvalidAmountException e) {
            throw new RuntimeException(e);
        }
    }

    public void confirmWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawAmountTextField.getText());
            currentUser.getAccount(selectedAcc).withdraw(amount);
            withdrawConfirmationText.setText("Successfully Withdrawn: " + "$" +df.format(amount)+"\nCurrent Bal: " + "$" + df.format(currentUser.getAccount(selectedAcc).getBalance()));

            /*
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Confirm Withdraw");
            a.setContentText("Successfully Withdrawn: " + "$" +df.format(amount)+"\nCurrent Bal: " + "$" + df.format(currentUser.getAccount(selectedAcc).getBalance()));
            a.showAndWait();
            */
            //showHomePane();
        }
        //set our own exception cases & set alert to the e.getMessage()
        catch(Exception e){
            if(e instanceof IllegalArgumentException){
                withdrawConfirmationText.setText("Sorry Your Balance is Too Low");
            }
            else{
                withdrawConfirmationText.setText("Please Enter a Numeric Value");
            }
            withdrawConfirmationText.setStyle(errorStyle);
            withdrawAmountTextField.setText("");
        }
    }

    public void confirmTransfer() {
        //Local transfer
        try{
            double amount = Double.parseDouble(transferAmountTextField.getText());
            int transferToLocalAccVar = Integer.parseInt(transferToLocalAcc.getText());
            String memo = transferLocalMemo.getText();
            currentUser.getAccount(selectedAcc).transfer(currentUser.getAccount(transferToLocalAccVar), memo, amount);}
        catch(Exception e){
            transferConfirmationText.setText("Transfer Failed!");
            transferConfirmationText.setStyle(errorStyle);
        }
    }
    //NEED TO DO NON-LOCAL TRANSFER




    public void showHomePane() {
        populateAcc();
        setLabels();
        homePane.setVisible(true);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
    }

    public void showDepositPane() {
        homePane.setVisible(false);
        depositPane.setVisible(true);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
    }

    public void showWithdrawPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(true);
        transferPane.setVisible(false);
    }

    public void logout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginPage.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(bank_name);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/kek.jpg"))));
        stage.setScene(new Scene(root1));
        stage.setResizable(false);
        stage.show();
        Stage stage2 = (Stage) balance.getScene().getWindow();
        stage2.close();
    }


    public void populateAcc() {
        //System.out.println(currentUser.getAllAccountsUUID());
        ObservableList<String> items = FXCollections.observableArrayList(currentUser.getAllAccountsUUID());
        accLsOverview.setItems(items);
        accLsDeposit.setItems(items);
        accLsWithdraw.setItems(items);
        accLsTransfer.setItems(items);

    }

    public void handleAccClick(){
        accLsOverview.setOnMouseClicked(mouseEvent -> {String selectItem = accLsOverview.getSelectionModel().getSelectedItem().toString();
            selectedAcc = currentUser.getAllAccountsUUID().indexOf(selectItem);

            //update labels
            setLabels();
            /*
            Dialog d = new Alert(Alert.AlertType.INFORMATION,selectItem);
            d.show();
             */
        });
        accLsDeposit.setOnMouseClicked(mouseEvent -> {String selectItem = accLsDeposit.getSelectionModel().getSelectedItem().toString();
            selectedAcc = currentUser.getAllAccountsUUID().indexOf(selectItem);

            //update labels
            setLabels();
            /*
            Dialog d = new Alert(Alert.AlertType.INFORMATION,selectItem);
            d.show();
             */
        });
        accLsWithdraw.setOnMouseClicked(mouseEvent -> {String selectItem = accLsWithdraw.getSelectionModel().getSelectedItem().toString();
            selectedAcc = currentUser.getAllAccountsUUID().indexOf(selectItem);

            //update labels
            setLabels();
            /*
            Dialog d = new Alert(Alert.AlertType.INFORMATION,selectItem);
            d.show();
             */
        });
        accLsTransfer.setOnMouseClicked(mouseEvent -> {String selectItem = accLsTransfer.getSelectionModel().getSelectedItem().toString();
            selectedAcc = currentUser.getAllAccountsUUID().indexOf(selectItem);

            //update labels
            setLabels();
            /*
            Dialog d = new Alert(Alert.AlertType.INFORMATION,selectItem);
            d.show();
             */
        });
    }
}
