package com.example.oopatmuiv3;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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

// Dashboard controller for java fxml dashboardPage
// Declaring components in fxml with functions
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
    private Label accNumberSetting;
    @FXML
    private Label usrID;
    @FXML
    private Label balance;
    @FXML
    private Label countryLabel;
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
    private TextField transferAmountTextField;
    @FXML
    private Button transferButton;
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
    private ListView<String> accLsSetting;
    @FXML
    private TextField transferMemoField;
    @FXML
    private ComboBox<String> settingsCombo;
    @FXML
    private Pane changeWithdrawalLimit;
    @FXML
    private Pane changeTransferLimit;
    @FXML
    private Pane currencySetting;
    @FXML
    private Pane changePIN;
    @FXML
    private PasswordField oldPIN;
    @FXML
    private PasswordField newPIN;
    @FXML
    private PasswordField newReenterPin;
    @FXML
    private TextField withdrawLimitText;
    @FXML
    private TextField transferLimitText;
    @FXML
    private Label settingConfirmationText;
    @FXML
    private Label currencySymbolBefore;
    @FXML
    private Label currencyLabel;
    @FXML
    private ListView<String> currenciesListView;
    @FXML
    private Label currentWithdrawLimit;
    @FXML
    private Label currentTransferLimit;
    @FXML
    private Label currentWithdrawLimitCurrency;
    @FXML
    private Label currentTransferLimitCurrency;

    // Strings which hold css elements to easily re-use in the application
    String errorStyle = "-fx-text-fill: RED;";
    String successStyle = "-fx-text-fill: GREEN;";

    String bank_name = "kek";

    // Initialise User currentUser, selected Acc, selected Currency and DecimalFormat import
    protected User currentUser;
    private Integer selectedAcc = 0;
    private Integer selectedCurr = 0;
    DecimalFormat df = new DecimalFormat("0.00");


    // Set to display label component in fxml with database values
    @FXML
    protected void setLabels() {
        name.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        usrID.setText(currentUser.getUUID());
        accNumber.setText(currentUser.getAccount(selectedAcc).getUUID());
        countryLabel.setText(currentUser.getCountry());
        currencySymbolBefore.setText(currentUser.getAccount(0).getCurrency().getSymbolBefore());
        balance.setText(String.format("%.2f",currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getBalance())));
        ObservableList<String> transactions = FXCollections.observableArrayList(currentUser.getAccount(selectedAcc).getTransactionHistory());
        transactionLs.setItems(transactions);

        accNumberDeposit.setText(currentUser.getAccount(selectedAcc).getUUID());
        accNumberWithdraw.setText(currentUser.getAccount(selectedAcc).getUUID());
        accNumberTransfer.setText(currentUser.getAccount(selectedAcc).getUUID());
        accNumberSetting.setText(currentUser.getAccount(selectedAcc).getUUID());

        ObservableList<String> items = FXCollections.observableArrayList(currentUser.getAllAccountsUUID());
        accLsOverview.setItems(items);
        accLsDeposit.setItems(items);
        accLsWithdraw.setItems(items);
        accLsTransfer.setItems(items);
        accLsSetting.setItems(items);


        ObservableList<String> currencies = FXCollections.observableArrayList(currentUser.getAvailableCurrencies());
        currenciesListView.setItems(currencies);
        currencyLabel.setText(currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter());

        currentTransferLimit.setText(String.format("%.2f",currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getTransferLimit())));
        currentTransferLimitCurrency.setText(currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore());
        currentWithdrawLimit.setText(String.format("%.2f",currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getWithdrawLimit())));
        currentWithdrawLimitCurrency.setText(currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore());


        //reset input labels & confirmation labels
        depositAmountTextField.setText("");
        withdrawAmountTextField.setText("");
        transferAmountTextField.setText("");
        recieverTextField.setText("");
        transferMemoField.setText("");
        withdrawLimitText.setText("");
        transferLimitText.setText("");
        oldPIN.setText("");
        newPIN.setText("");
        newReenterPin.setText("");
        depositConfirmationText.setText("");
        withdrawConfirmationText.setText("");
        transferConfirmationText.setText("");

        if (!settingsPane.isVisible()){
            settingConfirmationText.setText("");
        }
    }

    // button callback to deposit. excpetions are caught and display red errors (elaborated more in test case)
    @FXML
    protected void confirmDeposit() {
        try{
            double amount = Double.parseDouble(depositAmountTextField.getText());
            currentUser.getAccount(selectedAcc).deposit(amount);
            depositConfirmationText.setText("Successfully Deposited: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " +df.format(amount)+  " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter() +"\nCurrent Bal: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getBalance())) + " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter());
            depositConfirmationText.setStyle(successStyle);

        }
        //set our own exception cases & set alert to the e.getMessage()
        catch(NumberFormatException e){
            depositConfirmationText.setText("Please Enter a Numeric Value");
            depositConfirmationText.setStyle(errorStyle);
        } catch (InvalidAmountException e) {
            depositConfirmationText.setText(e.getMessage());
            depositConfirmationText.setStyle(errorStyle);
        }
        catch (InvalidNoteDepositException e){
            depositConfirmationText.setText(e.getMessage());
            depositConfirmationText.setStyle(errorStyle);
        }
    }
    // button callback to withdraw. excpetions are caught and display red errors (elaborated more in test case)
    @FXML
    protected void confirmWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawAmountTextField.getText());
            currentUser.getAccount(selectedAcc).withdraw(amount);
            withdrawConfirmationText.setText("Successfully Withdrawn: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " +df.format(amount)+  " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter() +"\nCurrent Bal: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getBalance())) + " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter());
            withdrawConfirmationText.setStyle(successStyle);

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
        catch (WithdrawLimitException e){
            withdrawConfirmationText.setText(e.getMessage());
            withdrawConfirmationText.setStyle(errorStyle);
        }
        catch (InvalidNoteWithdrawalException e){
            withdrawConfirmationText.setText(e.getMessage());
            withdrawConfirmationText.setStyle(errorStyle);
        }
    }
    // button callback to transfer. excpetions are caught and display red errors (elaborated more in test case)
    @FXML
    protected void confirmTransfer() {
        try{
            double amount = Double.parseDouble(transferAmountTextField.getText());
            String toAcc = recieverTextField.getText();
            String memo = transferMemoField.getText();
            currentUser.getAccount(selectedAcc).transfer(toAcc,memo,amount);
            transferConfirmationText.setText("Successful transfer from "+currentUser.getAccount(selectedAcc).getUUID()+" to "+toAcc);
            transferConfirmationText.setStyle(successStyle);


        }
        catch (NumberFormatException e){
            transferConfirmationText.setText("Please Enter a Numeric Value");
            transferConfirmationText.setStyle(errorStyle);
        }
        catch (InvalidWithdrawAndTransferAmountException e){
            transferConfirmationText.setText(e.getMessage());
            transferConfirmationText.setStyle(errorStyle);
        }
        catch (TransferLimitException e){
            transferConfirmationText.setText(e.getMessage());
            transferConfirmationText.setStyle(errorStyle);
        }
    }
    // button callback to update settings based on setting type. excpetions are caught and display red errors (elaborated more in test case)
    @FXML
    protected void confirmSettings(){
        try{
            double oldLimit;
            if (changeWithdrawalLimit.isVisible()){
                oldLimit = currentUser.getAccount(selectedAcc).getWithdrawLimit();
                double newLimit = currentUser.getAccount(selectedAcc).getCurrency().unconvert(Double.parseDouble(withdrawLimitText.getText()));;
                currentUser.getAccount(selectedAcc).changeLimit("localWithdrawLimit", newLimit);
                settingConfirmationText.setText("Successful change of Withdrawal Limit from " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(oldLimit))+" to " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " "  + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getWithdrawLimit())));
                settingConfirmationText.setStyle(successStyle);
            }
            else if (changeTransferLimit.isVisible()){
                oldLimit = currentUser.getAccount(selectedAcc).getTransferLimit();
                double newLimit = currentUser.getAccount(selectedAcc).getCurrency().unconvert(Double.parseDouble(transferLimitText.getText()));;
                currentUser.getAccount(selectedAcc).changeLimit("localTransferLimit", newLimit);
                settingConfirmationText.setText("Successful change of Transfer Limit from " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(oldLimit))+" to " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " "  + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getTransferLimit())));
                settingConfirmationText.setStyle(successStyle);
            }
            else if (currencySetting.isVisible()){
                currencyLabel.setText(currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter());
                ArrayList<String> countryLs = currentUser.getCountries();
                currentUser.setCountry(countryLs.get(selectedCurr));
                settingConfirmationText.setText("Successfully set Currency to "+currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter());
                settingConfirmationText.setStyle(successStyle);
            }
            else if (changePIN.isVisible()){
                String old = oldPIN.getText();
                String newP = newPIN.getText();
                String newP2 = newReenterPin.getText();
                // change pin of uer
                currentUser.changePin(old, newP, newP2);
                settingConfirmationText.setText("Successfully change PIN.");
                settingConfirmationText.setStyle(successStyle);
            }
            else{
                settingConfirmationText.setText("Please select a setting");
                settingConfirmationText.setStyle(errorStyle);
            }
        }
        catch (NumberFormatException e){
            settingConfirmationText.setText("Please Enter a Numeric Value");
            settingConfirmationText.setStyle(errorStyle);
        } catch (InvalidNewPinException e) {
            settingConfirmationText.setText(e.getMessage());
            settingConfirmationText.setStyle(errorStyle);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidOldPinException e) {
            settingConfirmationText.setText(e.getMessage());
            settingConfirmationText.setStyle(errorStyle);
        }

        setLabels();
    }

    // button callback to display home pane
    @FXML
    protected void showHomePane() {
        setLabels();
        homePane.setVisible(true);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);

    }
    // button callback to display deposit pane
    @FXML
    protected void showDepositPane() {
        setLabels();
        homePane.setVisible(false);
        depositPane.setVisible(true);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);
    }
    // button callback to display withdraw pane
    @FXML
    protected void showWithdrawPane() {
        setLabels();
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(true);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);
    }
    // button callback to display transfer pane
    @FXML
    protected void showTransferPane() {
        setLabels();
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(true);
        settingsPane.setVisible(false);
    }
    // button callback to display settings pane
    @FXML
    protected void showSettingsPane() {
        setLabels();
        settingsCombo.getSelectionModel().selectFirst();
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(true);
    }
    // logout button to return to login page
    @FXML
    protected void logout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginPage.fxml"));
        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(bank_name);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/kek.png"))));
        stage.setScene(new Scene(root1));
        stage.setResizable(false);
        stage.show();
        Stage stage2 = (Stage) balance.getScene().getWindow();
        stage2.close();
    }
    // callback from fxml to get account selection
    @FXML
    protected void handleAccClick(){
        getLsViewClick(accLsOverview);
        getLsViewClick(accLsDeposit);
        getLsViewClick(accLsWithdraw);
        getLsViewClick(accLsTransfer);
        getLsViewClick(accLsSetting);
    }
    // getting account selection from listview
    @FXML
    private void getLsViewClick(ListView<String> lsView) throws NullPointerException{
        lsView.setOnMouseClicked(mouseEvent -> {
            try{
                String selectItem = lsView.getSelectionModel().getSelectedItem().toString();
                selectedAcc = currentUser.getAllAccountsUUID().indexOf(selectItem);
            }catch (NullPointerException e){} //ignore if click empty option

            //update labels
            setLabels();
        });
    }
    // getting currency selection from listview
    @FXML
    protected void handleCurrencyClick(){
        currenciesListView.setOnMouseClicked(mouseEvent -> {
            try{
                String selectItem = currenciesListView.getSelectionModel().getSelectedItem().toString();
                selectedCurr = currentUser.getAvailableCurrencies().indexOf(selectItem);
            }catch (NullPointerException e){} //ignore if click empty option
            //update labels
            setLabels();
        });
    }
    // settings listener to change Stack Pane to each type of setting
    @FXML
    protected void settings(){
        settingsCombo.getSelectionModel().selectedItemProperty().addListener((selected, oldSetting, newSetting) -> {
            changeWithdrawalLimit.setVisible(false);
            changeTransferLimit.setVisible(false);
            currencySetting.setVisible(false);
            changePIN.setVisible(false);
            if (oldSetting != null){
                switch (oldSetting){
                    case "changeWithdrawalLimit":
                        changeWithdrawalLimit.setVisible(false);
                        break;
                    case "changeTransferLimit":
                        changeTransferLimit.setVisible(false);
                        break;
                    case "currencySetting":
                        currencySetting.setVisible(false);
                        break;
                    case "changePIN":
                        changePIN.setVisible(false);
                        break;

                }
            }
            if(newSetting != null){
                switch(newSetting) {
                    case "changeWithdrawalLimit":
                        changeWithdrawalLimit.setVisible(true);
                        break;
                    case "changeTransferLimit":
                        changeTransferLimit.setVisible(true);
                        break;
                    case "currencySetting":
                        currencySetting.setVisible(true);
                        break;
                    case "changePIN":
                        changePIN.setVisible(true);
                        break;
                }
            }
        });
    }

}
