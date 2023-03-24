package com.example.oopatmuiv3;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import javafx.scene.control.Alert;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Account {
    private String name;
    private double balance;
    private String uuid;
    private String holderUUID;
    private ArrayList<Transaction> transactions;
    private Bank bank;
    private User user;
    private double localTransferLimit, overseasTransferLimit, localWithdrawLimit, overseasWithdrawLimit;
    private Currency currency;

    private Date lastTransactionTime;

    private double todayAmount;

    protected Account(String name, String holder, Bank bank, User user) {
        this.name = name;
        this.holderUUID = holder;
        this.uuid = bank.getNewAccountUUID();
        this.balance = 0;
        this.bank = bank;
        this.user = user;
        this.transactions = new ArrayList<>();
        this.localTransferLimit = 1000;
        this.overseasTransferLimit = 1000;
        this.localWithdrawLimit = 1000;
        this.overseasWithdrawLimit = 1000;
        this.currency = null;
        this.lastTransactionTime = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.todayAmount = 0;
    }

    protected String getSummaryLine() throws Exception {
        double balance = this.getBalance();
        if (balance >= 0) {
            return String.format("%s : %s %.02f %s : %s", this.uuid, this.currency.getSymbolBefore(),this.currency.convert(balance), this.currency.getSymbolAfter(),this.name);
        } else {
            return String.format("%s : %s %.02f %s : %s", this.uuid,this.currency.getSymbolBefore(), this.currency.convert(balance),this.currency.getSymbolAfter() ,this.name);
        }
    }

    protected Account(String name, String holder, String uuid, ArrayList<Transaction> transactions, Bank bank, double balance, User user) {
        this.uuid = uuid;
        this.name = name;
        this.holderUUID = holder;
        this.balance = balance;
        this.transactions = transactions;
        this.bank = bank;
        this.localTransferLimit = 1000;
        this.overseasTransferLimit = 1000;
        this.localWithdrawLimit = 1000;
        this.overseasWithdrawLimit = 1000;
        this.user = user;
        this.lastTransactionTime = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.todayAmount = 0;

    }

    protected Currency getCurrency() {
        return this.currency;
    }

    protected void setCurrency(Currency c) {
        this.currency = c;
    }

    protected String getUUID() {
        return this.uuid;
    }

    protected double getLocalTransferLimit() {
        return this.localTransferLimit;
    }

    protected double getOverseasTransferLimit() {
        return this.overseasTransferLimit;
    }

    protected double getLocalWithdrawLimit() {
        return this.localWithdrawLimit;
    }

    protected double getOverseasWithdrawLimit() {
        return this.overseasWithdrawLimit;
    }

    protected double getTodayAmount() { return this.todayAmount;}

    protected Date getLastTransactionTime() { return this.getLastTransactionTime();}

    protected void setLocalTransferLimit(double newLimit) {
        this.localTransferLimit = newLimit;
    }

    protected void setOverseasTransferLimit(double newLimit) {
        this.overseasTransferLimit = newLimit;
    }

    protected void setLocalWithdrawLimit(double newLimit) {
        this.localWithdrawLimit= newLimit;
    }

    protected void setOverseasWithdrawLimit(double newLimit) {
        this.overseasWithdrawLimit = newLimit;
    }

    protected void setLastTransactionTime(Date date) { this.lastTransactionTime = date; }

    protected void setTodayAmount(double amount) { this.todayAmount = amount; }

    protected void setUUID(String newUUID) {
        this.uuid = newUUID;
    }

    protected String getName() {
        return this.name;
    }

    protected String getHolder() {
        return this.holderUUID;
    }

    protected double getBalance() {
        return this.balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    protected User getUser() {
        return this.user;
    }

    protected Account getAccount(String number) {
        ArrayList<Account> accounts = new ArrayList<>();
        //System.out.println("Number of accounts" + this.getUser().numOfAccounts());
        int numOfAcc = this.getUser().numOfAccounts();
        for (int i = 0; i < numOfAcc; i++) {
            //System.out.println(this.getUser().getAccount(i).getUUID());
            if (this.getUser().getAccount(i).getUUID().equals(number)) {
                //System.out.println("SAME!");
                return this.getUser().getAccount(i);
            }
        }
        return this;

    }

    protected void deposit(double amount) throws InvalidAmountException, InvalidNoteDepositException {
        if (amount <= 0 || amount == -0 || (BigDecimal.valueOf(amount).scale() > 2)) {
            throw new InvalidAmountException(amount);
        }
        else if (!(amount%2 == 0 || amount%5 == 0)){
            throw new InvalidNoteDepositException();
        }

        balance += amount;
        this.modifyBalance(balance);
        addTransaction(this, new Transaction(amount, "Deposit", this.getUUID()));
        //this.transactions.add(new Transaction(amount, "Deposit", this.getUUID()));
    }
    protected boolean addTransactionNoDb(Transaction transaction) {
        this.transactions.add(transaction);
        return true;
    }
    protected boolean addTransaction(Account account, Transaction transaction) {
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

    protected boolean updateAccount(Date newDate, double todayAmount) {
        try {
            MongoCollection<Document> accountCollection = this.bank.database.getCollection("accounts");
            Bson filter = Filters.eq("_id", this.getUUID());
            Bson updateOperation = new Document("$set", new Document()
                    .append("todayAmount", todayAmount)
                    .append("lastTransactionTime", newDate));
            accountCollection.updateOne(filter, updateOperation);
            return true;
        } catch (MongoException e) {
            return false;
        }
    }

    protected boolean changeTransferLimit(String type, double newLimit) {
        try {
            if (type == "localTransferLimit") {
                this.setLocalTransferLimit(newLimit);
            } else if (type == "overseasTransferLimit") {
                this.setOverseasTransferLimit(newLimit);
            } else if (type == "localWithdrawLimit") {
                this.setLocalWithdrawLimit(newLimit);
            } else if (type == "overseasWithdrawLimit") {
                this.setOverseasWithdrawLimit(newLimit);
            } else {
                return false;
            }
            MongoCollection <Document> accountCollection = this.bank.database.getCollection("accounts");
            Bson filter = Filters.eq("_id", this.getUUID());
            Bson updateOperation = new Document("$set", new Document(type, newLimit));
            accountCollection.updateOne(filter, updateOperation);
            return true;
        } catch (MongoException e) {
            return false;
        }
    }

    protected void printTransactionHistory() {
        System.out.printf("\nTransaction History for Account %s\n", this.uuid);
        for (int t = this.transactions.size()-1; t>0;t--) {
            try {
                System.out.println(this.transactions.get(t).getSummaryLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }
    protected ArrayList<String> getTransactionHistory() {
        ArrayList<String> transactionLs = new ArrayList<>();
        for (int t = this.transactions.size()-1; t>0;t--) {
            try {
                transactionLs.add(this.transactions.get(t).getSummaryLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transactionLs;
    }

    protected boolean modifyBalance(double balance) {
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

    protected boolean withdraw(double amount) throws InvalidWithdrawAndTransferAmountException, WithdrawLimitException, InvalidNoteWithdrawalException {
        if (amount <= 0 || amount == -0 || (BigDecimal.valueOf(amount).scale() > 2) || amount > balance) {
            throw new InvalidWithdrawAndTransferAmountException(amount, balance);
        }
        else if (amount%10 != 0){
            throw new InvalidNoteWithdrawalException();
        }
        else if (amount > this.getLocalWithdrawLimit()){
            throw new WithdrawLimitException(amount, this.getLocalWithdrawLimit());
        }

        balance -= amount;
        this.modifyBalance(balance);
        addTransaction(this, new Transaction(amount, "Withdrawal", this.getUUID()));
        //this.transactions.add(new Transaction(amount, "Withdrawal", this.getUUID()));
        return true;
    }

    protected ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    protected boolean receive(double amount) {
        try {
            if (amount <= 0) {
                throw new Exception("Amount must be greater than 0");
            }
        } catch (Exception e) {
            System.out.println("Error: Amount is negative" + e);
        }
        balance += amount;
        this.modifyBalance(balance);
        return true;
    }

    protected double getOtherBal(String accountNumber) {
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

    protected boolean transfer(String accountNumber, String memo, double amount) throws InvalidWithdrawAndTransferAmountException, TransferLimitException
    {
        if (amount <= 0 || amount == -0 || (BigDecimal.valueOf(amount).scale() > 2) || amount > this.balance){
            throw new InvalidWithdrawAndTransferAmountException(amount, this.balance);
        }
        LocalDate currentDateLocalDate = LocalDate.now();
        Date currentDate = Date.from(currentDateLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        // check if day same, if same see is todayAmount + amount > limit
        //System.out.println(String.valueOf(currentDate.getDay())+ this.lastTransactionTime.getDay());
        if ((currentDate.getDay() != this.lastTransactionTime.getDay() && currentDate.getMonth() != this.lastTransactionTime.getMonth() && currentDate.getYear() != this.lastTransactionTime.getYear()) && (amount < this.localTransferLimit)) {
            this.todayAmount = amount;
            this.lastTransactionTime = currentDate;
            this.updateAccount(currentDate, amount);
        } else if ((currentDate.getDay() != this.lastTransactionTime.getDay() && currentDate.getMonth() != this.lastTransactionTime.getMonth() && currentDate.getYear() != this.lastTransactionTime.getYear()) && (amount > this.localTransferLimit)) {
            this.todayAmount = 0;
            this.updateAccount(currentDate, 0);
            throw new TransferLimitException(amount, this.getLocalTransferLimit());
        }
        else if ((currentDate.getDay() == this.lastTransactionTime.getDay() && currentDate.getMonth() == this.lastTransactionTime.getMonth() && currentDate.getYear() == this.lastTransactionTime.getYear()) && (this.todayAmount+amount > this.localTransferLimit)) {
            throw new TransferLimitException(amount, this.getLocalTransferLimit());
        } else {
            this.lastTransactionTime = currentDate;
            double newAmount = this.todayAmount+amount;
            this.todayAmount = newAmount;
            this.updateAccount(currentDate, newAmount);
        }
        this.balance -= amount;
        this.modifyBalance(this.balance);
        //addTransaction(this, new Transaction(amount, "Transfer to OTHER " + accountNumber + " - "+memo, this.getUUID()));
        if (this.user.getAllAccountsUUID().contains(accountNumber)){
            addTransaction(this, new Transaction(amount, "Transfer to LOCAL " + accountNumber + " - "+memo, this.getUUID()));
        }
        else{
            addTransaction(this, new Transaction(amount, "Transfer to OTHER " + accountNumber + " - "+memo, this.getUUID()));
        }
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
                    //Transaction transaction = new Transaction(amount, "Transfer from OTHER " + accountNumber + " - "+memo, this.uuid);
                    Transaction transaction;
                    if (this.user.getAllAccountsUUID().contains(accountNumber)){
                        transaction = new Transaction(amount, "Transfer from LOCAL " + this.getUUID() + " - "+memo, this.getUUID());
                    }
                    else {
                        transaction = new Transaction(amount, "Transfer from OTHER " + this.getUUID() + " - "+memo, this.getUUID());
                    }
                    Account a = this.getAccount(accountNumber);
                    if (!a.getUUID().equals(this.getUUID())) {
                        a.addTransactionNoDb(transaction);
                    };
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

    //transfer to local only
    /*
    protected boolean transfer(Account destination, String memo, double amount) {
        try {
            if (balance < amount) {
                throw new Exception("Amount must be greater than 0");
            } else if (amount <= 0) {
                throw new Exception("Amount must be greater than 0");
            }
        } catch (Exception e) {
            System.out.println("Error: Amount is negative" + e);
        }
        balance -= amount;
        this.modifyBalance(balance);
        addTransaction(this, new Transaction(amount, "Transfer to " + destination.getUUID() + " - "+memo, this.getUUID()));
        destination.receive(amount);
        destination.addTransaction(destination, new Transaction(amount, "Transfer from " + this.getUUID() + " - "+memo, destination.getUUID()));
        return true;
    }
     */

}
