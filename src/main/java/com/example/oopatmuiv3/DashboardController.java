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
    private Pane changeLocalWithdrawalLimit;
    @FXML
    private Pane changeLocalTransferLimit;
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
    private TextField localWithdrawLimitText;
    @FXML
    private TextField localTransferLimitText;
    @FXML
    private TextField overseasWithdrawalLimitText;
    @FXML
    private TextField overseasTransferLimitText;
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




    String errorStyle = "-fx-text-fill: RED;";
    String successStyle = "-fx-text-fill: GREEN;";

    String bank_name = "kek";
    protected User currentUser;
    private Integer selectedAcc = 0;
    private Integer selectedCurr = 0;
    DecimalFormat df = new DecimalFormat("0.00");



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

        currentTransferLimit.setText(String.format("%.2f",currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getLocalTransferLimit())));
        currentTransferLimitCurrency.setText(currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore());
        currentWithdrawLimit.setText(String.format("%.2f",currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getLocalWithdrawLimit())));
        currentWithdrawLimitCurrency.setText(currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore());


        //reset input labels & confirmation labels
        depositAmountTextField.setText("");
        withdrawAmountTextField.setText("");
        transferAmountTextField.setText("");
        recieverTextField.setText("");
        transferMemoField.setText("");
        localWithdrawLimitText.setText("");
        localTransferLimitText.setText("");
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

    @FXML
    protected void confirmDeposit() {
        try{
            double amount = Double.parseDouble(depositAmountTextField.getText());
            currentUser.getAccount(selectedAcc).deposit(amount);
            depositConfirmationText.setText("Successfully Deposited: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " +df.format(amount)+  " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter() +"\nCurrent Bal: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getBalance())) + " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter());
            depositConfirmationText.setStyle(successStyle);


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
        catch (InvalidNoteDepositException e){
            depositConfirmationText.setText(e.getMessage());
            depositConfirmationText.setStyle(errorStyle);
        }
    }
    @FXML
    protected void confirmWithdraw() {
        try {
            double amount = Double.parseDouble(withdrawAmountTextField.getText());
            currentUser.getAccount(selectedAcc).withdraw(amount);
            withdrawConfirmationText.setText("Successfully Withdrawn: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " +df.format(amount)+  " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter() +"\nCurrent Bal: " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getBalance())) + " " +currentUser.getAccount(selectedAcc).getCurrency().getSymbolAfter());
            withdrawConfirmationText.setStyle(successStyle);


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
        catch (WithdrawLimitException e){
            withdrawConfirmationText.setText(e.getMessage());
            withdrawConfirmationText.setStyle(errorStyle);
        }
        catch (InvalidNoteWithdrawalException e){
            withdrawConfirmationText.setText(e.getMessage());
            withdrawConfirmationText.setStyle(errorStyle);
        }
    }
    @FXML
    protected void confirmTransfer() {
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
    @FXML
    protected void confirmSettings(){
        try{
            double oldLimit;
            if (changeLocalWithdrawalLimit.isVisible()){
                oldLimit = currentUser.getAccount(selectedAcc).getLocalWithdrawLimit();
                double newLimit = currentUser.getAccount(selectedAcc).getCurrency().unconvert(Double.parseDouble(localWithdrawLimitText.getText()));;
                currentUser.getAccount(selectedAcc).changeTransferLimit("localWithdrawLimit", newLimit);
                settingConfirmationText.setText("Successful change of Withdrawal Limit from " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(oldLimit))+" to " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " "  + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getLocalWithdrawLimit())));
                settingConfirmationText.setStyle(successStyle);
            }
            else if (changeLocalTransferLimit.isVisible()){
                oldLimit = currentUser.getAccount(selectedAcc).getLocalTransferLimit();
                double newLimit = currentUser.getAccount(selectedAcc).getCurrency().unconvert(Double.parseDouble(localTransferLimitText.getText()));;
                currentUser.getAccount(selectedAcc).changeTransferLimit("localTransferLimit", newLimit);
                settingConfirmationText.setText("Successful change of Transfer Limit from " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " " + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(oldLimit))+" to " + currentUser.getAccount(selectedAcc).getCurrency().getSymbolBefore() + " "  + df.format(currentUser.getAccount(selectedAcc).getCurrency().convert(currentUser.getAccount(selectedAcc).getLocalTransferLimit())));
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
                //function go brrr
                currentUser.changePin(old, newP, newP2);
                settingConfirmationText.setText("Successfully change PIN.");
                settingConfirmationText.setStyle(successStyle);
                //rmb to catch exception & set text (e.g. oldPin wrong, newPIN & reenterPIN match, oldPIN=newPIN etc.)

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





    @FXML
    protected void showHomePane() {
        setLabels();
        homePane.setVisible(true);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);

    }
    @FXML
    protected void showDepositPane() {
        setLabels();
        homePane.setVisible(false);
        depositPane.setVisible(true);
        withdrawPane.setVisible(false);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);
    }
    @FXML
    protected void showWithdrawPane() {
        setLabels();
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(true);
        transferPane.setVisible(false);
        settingsPane.setVisible(false);
    }
    @FXML
    protected void showTransferPane() {
        setLabels();
        homePane.setVisible(false);
        depositPane.setVisible(false);
        withdrawPane.setVisible(false);
        transferPane.setVisible(true);
        settingsPane.setVisible(false);
    }
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

    @FXML
    protected void handleAccClick(){
        getLsViewClick(accLsOverview);
        getLsViewClick(accLsDeposit);
        getLsViewClick(accLsWithdraw);
        getLsViewClick(accLsTransfer);
        getLsViewClick(accLsSetting);


    }
    @FXML
    private void getLsViewClick(ListView<String> lsView) throws NullPointerException{
        lsView.setOnMouseClicked(mouseEvent -> {
            try{
                String selectItem = lsView.getSelectionModel().getSelectedItem().toString();
                selectedAcc = currentUser.getAllAccountsUUID().indexOf(selectItem);
            }catch (NullPointerException e){} //ignore if click empty option

            //update labels
            setLabels();
            /*
            Dialog d = new Alert(Alert.AlertType.INFORMATION,selectItem);
            d.show();
             */
        });
    }
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
    @FXML
    protected void settings(){
        settingsCombo.getSelectionModel().selectedItemProperty().addListener((selected, oldSetting, newSetting) -> {
            changeLocalWithdrawalLimit.setVisible(false);
            changeLocalTransferLimit.setVisible(false);
            currencySetting.setVisible(false);
            changePIN.setVisible(false);
            if (oldSetting != null){
                switch (oldSetting){
                    case "changeLocalWithdrawalLimit":
                        changeLocalWithdrawalLimit.setVisible(false);
                        break;
                    case "changeLocalTransferLimit":
                        changeLocalTransferLimit.setVisible(false);
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
                    case "changeLocalWithdrawalLimit":
                        changeLocalWithdrawalLimit.setVisible(true);
                        break;
                    case "changeLocalTransferLimit":
                        changeLocalTransferLimit.setVisible(true);
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
