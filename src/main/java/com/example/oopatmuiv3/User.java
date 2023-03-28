package com.example.oopatmuiv3;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

// Declares variables for user class
public class User {
    private String uuid;
    private String firstName;
    private String lastName;
    private String pinHash;
    private ArrayList<Account> accounts;
    private Bank bank;
    private String country;

    // this user constructor is used for creating a new user object
    protected User(String firstName, String lastName, String pinHash, Bank bank, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinHash = pinHash;
        this.accounts = new ArrayList<>();
        this.uuid = bank.getNewUserUUID();
        this.bank = bank;
        this.country = country;
    }

    // this hashPin method is used to hash the pin of the user
    protected static String hashPin(String pin) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] pinBytes = String.valueOf(pin).getBytes();
        return toHex(md.digest(pinBytes));
    }

    // this method is to set a new uuid for the user object
    protected void setUUID(String newUUID) {
        this.uuid = newUUID;
    }

    protected static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    // this is to get the user uid from the database
    protected String getUUID() {
        return this.uuid;
    }

    // this is to get the user pin from the database
    protected String getPinHash() {
        return this.pinHash;
    }

    // getCountry is used to get the country of the user from mongoDB
    protected String getCountry() {
        return this.country;
    }

    // this is to get the first name of the user from the database
    protected String getFirstName() {
        return this.firstName;
    }

    // this is to get the last name of the user from the database
    protected String getLastName() {
        return this.lastName;
    }

    // this is to set the bank accounts for the user
    protected void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    // setPin is used to set the pin of the user
    protected void setPin(String newPinHash) {
        this.pinHash = newPinHash;
    }

    // this changePin function is to update the pin of the user
    // check if the old pin is the same as the current pin that the user has entered
    // if it is currect, then check if the new pin matches the new pin that the user has retyped
    // it will then update the database
    protected boolean changePin(String oldPin, String newPin, String newPin2) throws NoSuchAlgorithmException, InvalidOldPinException, InvalidNewPinException {
        if (validatePin(oldPin)) {
            //System.out.println("Old pin correct");
            if ((newPin.matches("\\d{4}")) && (newPin.equals(newPin2))) {
                //System.out.println("hello new pin");
                String newPinHash = hashPin(newPin);
                setPin(newPinHash);
                try {
                    MongoCollection<Document> userCollection = this.bank.database.getCollection("users");
                    Bson filter = Filters.eq("_id", this.getUUID());
                    Bson updateOperation = new Document("$set", new Document("pinHash", newPinHash));
                    userCollection.updateOne(filter, updateOperation);
                    return true;
                } catch (MongoException e) {
                    return false;
                }
            } else {
                throw new InvalidNewPinException();
            }
//
        } else {
            throw new InvalidOldPinException();
        }
    }

    // this is to print the transaction history of the user from the database
    protected void printTransactionHistory(int acctIndex) {
        this.accounts.get(acctIndex).printTransactionHistory();
    }

    // this function is to add a new account for the user in the database
    protected Account addAccount(String name, Bank bank) {
        Account account = new Account(name, this.getUUID(), bank, this);
        MongoCollection<Document> accountCollection = this.bank.database.getCollection("accounts");
        Document accountDocument = new Document("_id", account.getUUID())
                .append("name", name)
                .append("holderUUID", account.getHolder())
                .append("balance", (double)0)
                .append("localTransferLimit", account.getTransferLimit())
                .append("localWithdrawLimit", account.getWithdrawLimit());
        accountCollection.insertOne(accountDocument);
        this.accounts.add(account);
        MongoCollection<Document> uuidsCollection = this.bank.database.getCollection("uuids");
        Document newDoc = new Document().append("uuid", account.getUUID());
        uuidsCollection.insertOne(newDoc);

        return account;
    }

    // this function is to get the number of accounts that the user has
    protected int numOfAccounts() {
        return this.accounts.size();
    }

    // This function takes in the user selection and returns the account based on the index
    protected Account getAccount(int index) {
        return this.accounts.get(index);
    }

    // This function is to print the account summary of the user
    // for every account, it prints the account number and the account summary
    protected void printAccountSummary() throws Exception {
        System.out.printf("\n\n%s 's Accounts Summary", this.firstName);
        for (int a = 0;a<this.accounts.size();a++) {
            System.out.printf("\n%d) %s", a+1, this.getAccount(a).getSummaryLine());
        }
        System.out.println();
    }

    // this is to check if the hashing of the pin that the user has entered is the same as the pin that is stored in the database
    protected boolean validatePin(String pin) {
        try {
            String currentPinHash = hashPin(pin);
            //System.out.println(currentPinHash);
            if (currentPinHash.equals(this.getPinHash())) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    // get account balance will return the balance of the account that the user has selected based on their input
    protected double getAccountBalance(int acctIndex) {
        return this.accounts.get(acctIndex).getBalance();
    }

    // this function get all the accounts that the user has in the database
    protected ArrayList<Account> getAllAccounts() {
        return this.accounts;

    }

    // this function is to get all the accounts that belongs to the uuid of the user
    protected ArrayList<String> getAllAccountsUUID(){
        ArrayList<String> ls = new ArrayList<>();
        for(int i = 0; i<accounts.size(); i++){
            ls.add(accounts.get(i).getUUID());
        }
        return ls;
    }

    // this function is to get all the currencies available
    protected ArrayList<String> getAvailableCurrencies(){
        ArrayList<String> currencyLs = new ArrayList<String>();
        for (int i = 0; i < this.bank.getCurrencies().size(); i++) {
            currencyLs.add(this.bank.getCurrencies().get(i).getCountry()+
                    " : "+ this.bank.getCurrencies().get(i).getSymbolAfter()+
                    " : "+ this.bank.getCurrencies().get(i).getSymbolBefore()+
                    " : "+ this.bank.getCurrencies().get(i).getExchangeRate());
        }
        return currencyLs;
    }

    // this function is to get all the countries available
    protected ArrayList<String> getCountries(){
        ArrayList<String> countryLs = new ArrayList<String>();
        for (int i = 0; i < this.bank.getCurrencies().size(); i++) {
            countryLs.add(this.bank.getCurrencies().get(i).getCountry());
        }
        return countryLs;
    }

    // changes the country to change currency
    protected boolean changeCountryNoDb(String country){
        this.country = country;
        Integer noOfAcc = this.accounts.size();
        for (int i = 0; i<noOfAcc; i++){
            Account a = this.getAccount(i);
            for (Currency currency: this.bank.getCurrencies()) {
                if (country.equalsIgnoreCase(currency.getCountry())) {
                    a.setCurrency(currency);
                }
            }
        }

        return true;
    }

    // this function is to update the country field in the users collection in the database
    protected boolean setCountry(String country) {
        try {
            MongoCollection<Document> userCollection = this.bank.database.getCollection("users");
            Bson filter = Filters.eq("_id", this.getUUID());
            Bson updateOperation = new Document("$set", new Document("country", country));
            userCollection.updateOne(filter, updateOperation);

            User u = this;
            u.changeCountryNoDb(country);
            return true;
        } catch (MongoException e) {
            return false;
        }
    }


}
