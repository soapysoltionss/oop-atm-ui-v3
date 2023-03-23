package com.example.oopatmuiv3;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private Label accNumberDeposit;
    @FXML
    private Label accNumberWithdraw;
    @FXML
    private Label accNumberTransfer;
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
    private Pane settingsPane;
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
    private ListView<String> accLsChangeSetting;
    @FXML
    private TextField transferMemoField;
    @FXML
    private ComboBox<String> settingsCombo;
    @FXML
    private Pane changeLocalWithdrawalLimit;
    @FXML
    private Pane changeLocalDepositLimit;
    @FXML
    private Pane changeOverseasDepositLimit;
    @FXML
    private Pane changeOverseasWithdrawalLimit;
    @FXML
    private Pane currencySetting;
    String errorStyle = "-fx-text-fill: RED;";
    String successStyle = "-fx-text-fill: GREEN;";

    String bank_name = "kek";
    protected User currentUser;
    private Integer selectedAcc = 0;
    DecimalFormat df = new DecimalFormat("0.00");



    public void setLabels() {
        name.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        usrID.setText(currentUser.getUUID());
        accNumber.setText(currentUser.getAccount(selectedAcc).getUUID());
        balance.setText(String.format("$" + "%.2f",currentUser.getAccount(selectedAcc).getBalance()));
        ObservableList<String> transactions = FXCollections.observableArrayList(currentUser.getAccount(selectedAcc).getTransactionHistory());
        transactionLs.setItems(transactions);

        accNumberDeposit.setText(currentUser.getAccount(selectedAcc).getUUID());
        accNumberWithdraw.setText(currentUser.getAccount(selectedAcc).getUUID());
        accNumberTransfer.setText(currentUser.getAccount(selectedAcc).getUUID());

        ObservableList<String> items = FXCollections.observableArrayList(currentUser.getAllAccountsUUID());
        accLsOverview.setItems(items);
        accLsDeposit.setItems(items);
        accLsWithdraw.setItems(items);
        accLsTransfer.setItems(items);
        //accLsChangeSetting.setItems(items);
    }


    public void confirmDeposit() {
        try{
            double amount = Double.parseDouble(depositAmountTextField.getText());
            currentUser.getAccount(selectedAcc).deposit(amount);
            depositConfirmationText.setText("Successfully Deposited: " + " $" + df.format(amount) + "\nCurrent Bal: " + "$" + df.format(currentUser.getAccount(selectedAcc).getBalance()));


            depositAmountTextField.setText("");
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
        } catch (InvalidAmountException e) {
            depositConfirmationText.setText(e.getMessage());
            depositConfirmationText.setStyle(errorStyle);
        }
    }

    public void confirmWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawAmountTextField.getText());
            currentUser.getAccount(selectedAcc).withdraw(amount);
            withdrawConfirmationText.setText("Successfully Withdrawn: " + "$" +df.format(amount)+"\nCurrent Bal: " + "$" + df.format(currentUser.getAccount(selectedAcc).getBalance()));


            withdrawAmountTextField.setText("");
            /*
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Confirm Withdraw");
            a.setContentText("Successfully Withdrawn: " + "$" +df.format(amount)+"\nCurrent Bal: " + "$" + df.format(currentUser.getAccount(selectedAcc).getBalance()));
            a.showAndWait();
            */
            //showHomePane();
        }
        //set our own exception cases & set alert to the e.getMessage()
        catch (NumberFormatException e){
            withdrawConfirmationText.setText("Please Enter a Numeric Value");
            withdrawConfirmationText.setStyle(errorStyle);
        }
        catch(InvalidWithdrawAndTransferAmountException e){
            withdrawConfirmationText.setText(e.getMessage());
            withdrawConfirmationText.setStyle(errorStyle);
        }
    }

    public void confirmTransfer() {
        /*
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
         */
        try{
            double amount = Double.parseDouble(transferAmountTextField.getText());
            String toAcc = recieverTextField.getText();
            String memo = transferMemoField.getText();
            currentUser.getAccount(selectedAcc).otherTransfer(toAcc,memo,amount);
            transferConfirmationText.setText("Successful transfer from "+currentUser.getAccount(selectedAcc).getUUID()+" to "+toAcc);


            transferAmountTextField.setText("");
            recieverTextField.setText("");
            transferMemoField.setText("");
        }
        catch (NumberFormatException e){
            transferConfirmationText.setText("Please Enter a Numeric Value");
            transferConfirmationText.setStyle(errorStyle);
        }
        catch(InvalidWithdrawAndTransferAmountException e){
            transferConfirmationText.setText(e.getMessage());
            transferConfirmationText.setStyle(errorStyle);
        }
    }




    public void showHomePane() {
        setLabels();
        homePane.setVisible(true);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);

    }

    public void showDepositPane() {
        homePane.setVisible(false);
        depositPane.setVisible(true);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);
    }

    public void showWithdrawPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(true);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);
    }

    public void showTransferPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(true);
        settingsPane.setVisible(false);
    }
    public void showSettingsPane() {
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(true);
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
        //accLsChangeSetting.setOnMouseClicked(mouseEvent -> {String selectItem = accLsChangeSetting.getSelectionModel().getSelectedItem().toString();
            //selectedAcc = currentUser.getAllAccountsUUID().indexOf(selectItem);

            //update labels
            //setLabels();
            /*
            Dialog d = new Alert(Alert.AlertType.INFORMATION,selectItem);
            d.show();
             */
        //});
    }

    public void settings(){
        settingsCombo.getSelectionModel().selectedItemProperty().addListener((selected, oldSetting, newSetting) -> {
            changeLocalWithdrawalLimit.setVisible(false);
            changeOverseasDepositLimit.setVisible(false);
            changeOverseasWithdrawalLimit.setVisible(false);
            changeLocalDepositLimit.setVisible(false);
            currencySetting.setVisible(false);
            if (oldSetting != null){
                switch (oldSetting){
                    case "changeLocalWithdrawalLimit":
                        changeLocalWithdrawalLimit.setVisible(false);
                        break;
                    case "changeLocalDepositLimit":
                        changeLocalDepositLimit.setVisible(false);
                        break;
                    case "changeOverseasWithdrawalLimit":
                        changeOverseasDepositLimit.setVisible(false);
                        break;
                    case "changeOverseasDepositLimit":
                        changeOverseasWithdrawalLimit.setVisible(false);
                        break;
                    case "currencySetting":
                        currencySetting.setVisible(false);
                        break;

                }
            }
            if(newSetting != null){
                switch(newSetting) {
                    case "changeLocalWithdrawalLimit":
                        changeLocalWithdrawalLimit.setVisible(true);
                        break;
                    case "changeLocalDepositLimit":
                        changeLocalDepositLimit.setVisible(true);
                        break;
                    case "changeOverseasWithdrawalLimit":
                        changeOverseasDepositLimit.setVisible(true);
                        break;
                    case "changeOverseasDepositLimit":
                        changeOverseasWithdrawalLimit.setVisible(true);
                        break;
                    case "currencySetting":
                        currencySetting.setVisible(true);
                        break;
                }
            }
        });
    }

}
