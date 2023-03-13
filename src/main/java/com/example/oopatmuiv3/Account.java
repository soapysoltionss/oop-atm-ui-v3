package com.example.oopatmuiv3;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class Account {
    private String name;
    private double balance;
    private String uuid;
    private String holderUUID;
    private ArrayList<Transaction> transactions;
    private Bank bank;
    private User user;

    public Account(String name, String holder, Bank bank, User user) {
        this.name = name;
        this.holderUUID = holder;
        this.uuid = bank.getNewAccountUUID();
        this.balance = 0;
        this.bank = bank;
        this.user = user;
        this.transactions = new ArrayList<>();
    }

    public String getSummaryLine() {
        double balance = this.getBalance();
        if (balance >= 0) {
            return String.format("%s : $%.02f : %s", this.uuid, balance, this.name);
        } else {
            return String.format("%s : $%.02f : %s", this.uuid, balance, this.name);
        }
    }

    public Account(String name, String holder, String uuid, ArrayList<Transaction> transactions, Bank bank, double balance) {
        this.uuid = uuid;
        this.name = name;
        this.holderUUID = holder;
        this.balance = balance;
        this.transactions = transactions;
        this.bank = bank;
    }

    public String getUUID() {
        return this.uuid;
    }

    public void setUUID(String newUUID) {
        this.uuid = newUUID;
    }

    public String getName() {
        return this.name;
    }

    public String getHolder() {
        return this.holderUUID;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        balance += amount;
        this.modifyBalance(balance);
        addTransaction(this, new Transaction(amount, "Deposit", this.getUUID()));
        //this.transactions.add(new Transaction(amount, "Deposit", this.getUUID()));
    }
    
    public boolean addTransaction(Account account, Transaction transaction) {
        this.transactions.add(transaction);
        try {
            MongoCollection<Document> transactionCollection = this.bank.database.getCollection("transactions");
        Document transactionDocument = new Document()
        .append("amount", transaction.getAmount())
        .append("memo", transaction.getMemo())
        .append("timestamp", transaction.getTimeStamp())
        .append("holder", account.getUUID());
        transactionCollection.insertOne(transactionDocument);
        return true;
        } catch(Exception e) {
            return false;
        }
    }

    
    public void printTransactionHistory() {
        System.out.printf("\nTransaction History for Account %s\n", this.uuid);
            for (int t = this.transactions.size()-1; t>0;t--) {
                System.out.println(this.transactions.get(t).getSummaryLine());
            }
            System.out.println();
    
    }

    public boolean modifyBalance(double balance) {
        try {
            MongoCollection<Document> accountCollection = this.bank.database.getCollection("accounts");
            Bson filter = Filters.eq("_id", this.getUUID());
            Bson updateOperation = new Document("$set", new Document("balance", balance));
            accountCollection.updateOne(filter, updateOperation);
            return true;
        } catch (MongoException e) {
            return false;
        }
    }

    public boolean withdraw(double amount) {
        if (balance < amount) {
            return false;
        } else {
            balance -= amount;
            this.modifyBalance(balance);
            addTransaction(this, new Transaction(amount, "Withdrawal", this.getUUID()));
            //this.transactions.add(new Transaction(amount, "Withdrawal", this.getUUID()));
            return true;
        }
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public boolean receive(double amount) {
        balance += amount;
        this.modifyBalance(balance);
        return true;
    }

    public double getOtherBal(String accountNumber) {
        MongoCollection<Document> accountCollection = this.bank.database.getCollection("accounts");
        Bson filter = Filters.eq("_id", accountNumber);
        double oldInfo = 0;
        for (Document doc: accountCollection.find(filter)) {
            String docBal = doc.getString("_id").toString();
            if (accountNumber.compareTo(docBal) == 0) {
                oldInfo = doc.getDouble("balance");
                return oldInfo;
            }
        }
        return -1;
    }

    public boolean otherTransfer(String accountNumber, String memo, double amount)
    {
        if (balance < amount) {
            return false;
        } else {
            balance -= amount;
            this.modifyBalance(balance);
            addTransaction(this, new Transaction(amount, "Transfer to OTHER " + accountNumber + " - "+memo, this.getUUID()));
            try {
                MongoCollection<Document> accountCollection = this.bank.database.getCollection("accounts");
                Bson filter = Filters.eq("_id", accountNumber);
                double oldInfo = getOtherBal(accountNumber);
                boolean done = false;
                if (oldInfo == -1) {
                    return false;
                } else {
                    done = true;
                }
                if (done == false) {
                    return false;
                } else {
                    Bson updateOperation = new Document("$set", new Document("balance", amount+oldInfo));
                    accountCollection.updateOne(filter, updateOperation);
                    try {
                        MongoCollection<Document> transactionCollection = this.bank.database.getCollection("transactions");
                    Transaction transaction = new Transaction(amount, "Transfer from OTHER " + accountNumber + " - "+memo, this.uuid);
                    Document transactionDocument = new Document()
                    .append("amount", transaction.getAmount())
                    .append("memo", transaction.getMemo())
                    .append("timestamp", transaction.getTimeStamp())
                    .append("holder", accountNumber);
                    transactionCollection.insertOne(transactionDocument);
                    return true;
                    } catch(Exception e) {
                        return false;
                    }
                }
            } catch (MongoException e) {
                return false;
            }            
        }
    }

    public boolean transfer(Account destination, String memo, double amount) {
        if (balance < amount) {
            return false;
        } 
        else {
            balance -= amount;
            this.modifyBalance(balance);
            addTransaction(this, new Transaction(amount, "Transfer to " + destination.getUUID() + " - "+memo, this.getUUID()));
            destination.receive(amount);
            destination.addTransaction(destination, new Transaction(amount, "Transfer from " + this.getUUID() + " - "+memo, destination.getUUID()));
            return true;
        }
    }
    
}
