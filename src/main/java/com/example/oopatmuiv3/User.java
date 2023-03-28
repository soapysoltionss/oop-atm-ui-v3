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

public class User {
    private String uuid;
    private String firstName;
    private String lastName;
    private String pinHash;
    private ArrayList<Account> accounts;
    private Bank bank;
    private String country;

    protected User(String firstName, String lastName, String pinHash, Bank bank, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinHash = pinHash;
        this.accounts = new ArrayList<>();
        this.uuid = bank.getNewUserUUID();
        this.bank = bank;
        this.country = country;
    }

    protected static String hashPin(String pin) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] pinBytes = String.valueOf(pin).getBytes();
        return toHex(md.digest(pinBytes));
    }




    protected void setUUID(String newUUID) {
        this.uuid = newUUID;
    }

    protected static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    protected String getUUID() {
        return this.uuid;
    }

    protected String getPinHash() {
        return this.pinHash;
    }

    protected String getCountry() {
        return this.country;
    }

    protected String getFirstName() {
        return this.firstName;
    }

    protected String getLastName() {
        return this.lastName;
    }

    protected void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    protected void setPin(String newPinHash) {
        this.pinHash = newPinHash;
    }

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

    protected void printTransactionHistory(int acctIndex) {
        this.accounts.get(acctIndex).printTransactionHistory();
    }


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

    protected int numOfAccounts() {
        return this.accounts.size();
    }

    protected Account getAccount(int index) {
        return this.accounts.get(index);
    }

    protected void printAccountSummary() throws Exception {
        System.out.printf("\n\n%s 's Accounts Summary", this.firstName);
        for (int a = 0;a<this.accounts.size();a++) {
            System.out.printf("\n%d) %s", a+1, this.getAccount(a).getSummaryLine());
        }
        System.out.println();
    }

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

    protected double getAccountBalance(int acctIndex) {
        return this.accounts.get(acctIndex).getBalance();
    }

    protected ArrayList<Account> getAllAccounts() {
        return this.accounts;

    }
    protected ArrayList<String> getAllAccountsUUID(){
        ArrayList<String> ls = new ArrayList<>();
        for(int i = 0; i<accounts.size(); i++){
            ls.add(accounts.get(i).getUUID());
        }
        return ls;
    }

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
    protected ArrayList<String> getCountries(){
        ArrayList<String> countryLs = new ArrayList<String>();
        for (int i = 0; i < this.bank.getCurrencies().size(); i++) {
            countryLs.add(this.bank.getCurrencies().get(i).getCountry());
        }
        return countryLs;
    }

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
