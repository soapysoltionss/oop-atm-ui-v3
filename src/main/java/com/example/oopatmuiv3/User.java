package com.example.oopatmuiv3;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class User {
    private String uuid;
    private String firstName;
    private String lastName;
    private String pinHash;
    private ArrayList<Account> accounts;
    private Bank bank;
    private String country;

    public User(String firstName, String lastName, String pinHash, Bank bank, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinHash = pinHash;
        this.accounts = new ArrayList<>();
        this.uuid = bank.getNewUserUUID();
        this.bank = bank;
        this.country = country;
    }

    public static String hashPin(String pin) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] pinBytes = String.valueOf(pin).getBytes();
        return toHex(md.digest(pinBytes));
    }


    public void setUUID(String newUUID) {
        this.uuid = newUUID;
    }

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    public String getUUID() {
        return this.uuid;
    }

    public String getPinHash() {
        return this.pinHash;
    }

    public String getCountry() {
        return this.country;
    }
    
    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public void printTransactionHistory(int acctIndex) {
        this.accounts.get(acctIndex).printTransactionHistory();
    }


    public Account addAccount(String name, Bank bank) {
        Account account = new Account(name, this.getUUID(), bank, this);
        MongoCollection<Document> accountCollection = this.bank.database.getCollection("accounts");
        Document accountDocument = new Document("_id", account.getUUID())
        .append("name", name)
        .append("holderUUID", account.getHolder())
        .append("balance", (double)0)
        .append("localTransferLimit", account.getLocalTransferLimit())
        .append("overseasTransferLimit", account.getOverseasTransferLimit())
        .append("localWithdrawLimit", account.getLocalWithdrawLimit())
        .append("overseasWithdrawLimit", account.getOverseasWithdrawLimit());
        accountCollection.insertOne(accountDocument);
        this.accounts.add(account);
        MongoCollection<Document> uuidsCollection = this.bank.database.getCollection("uuids");
        Document newDoc = new Document().append("uuid", account.getUUID());
        uuidsCollection.insertOne(newDoc);

        return account;
    }

    public int numOfAccounts() {
        return this.accounts.size();
    }

    public Account getAccount(int index) {
        return this.accounts.get(index);
    }

    public void printAccountSummary() throws Exception {
        System.out.printf("\n\n%s 's Accounts Summary", this.firstName);
        for (int a = 0;a<this.accounts.size();a++) {
            System.out.printf("\n%d) %s", a+1, this.getAccount(a).getSummaryLine());
        }
        System.out.println();
    }

    public boolean validatePin(String pin) {
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

    public double getAccountBalance(int acctIndex) {
        return this.accounts.get(acctIndex).getBalance();
    }

    public ArrayList<Account> getAllAccounts() {
        return this.accounts;
        
    }






}
