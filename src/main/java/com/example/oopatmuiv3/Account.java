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
import java.util.Collections;
import java.util.Date;


public class Account {
    // Declare variables
    private String name;
    private double balance;
    private String uuid;
    private String holderUUID;
    private ArrayList<Transaction> transactions;
    private Bank bank;
    private User user;
    private double transferLimit,  withdrawLimit;
    private Currency currency;

    private Date lastTransactionTime;

    private Date lastWithdrawalTime;

    private double todayAmount;

    private double withdrawTodayAmt;

    // this is the constructor that will take in string name, holder, bank object and user object
    protected Account(String name, String holder, Bank bank, User user) {
        this.name = name;
        this.holderUUID = holder;
        this.uuid = bank.getNewAccountUUID();
        this.balance = 0;
        this.bank = bank;
        this.user = user;
        this.transactions = new ArrayList<>();
        this.transferLimit = 1000;
        this.withdrawLimit = 1000;
        this.currency = null;
        this.lastTransactionTime = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.todayAmount = 0;
        this.lastWithdrawalTime = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.withdrawTodayAmt = 0;

    }

    // this function is to get uuid, the balance, symbol of the currency and the amount to display in CLI
    protected String getSummaryLine() throws Exception {
        double balance = this.getBalance();
        if (balance >= 0) {
            return String.format("%s : %s %.02f %s : %s", this.uuid, this.currency.getSymbolBefore(),this.currency.convert(balance), this.currency.getSymbolAfter(),this.name);
        } else {
            return String.format("%s : %s %.02f %s : %s", this.uuid,this.currency.getSymbolBefore(), this.currency.convert(balance),this.currency.getSymbolAfter() ,this.name);
        }
    }

    // get each account details for each user
    protected Account(String name, String holder, String uuid, ArrayList<Transaction> transactions, Bank bank, double balance, User user) {
        this.uuid = uuid;
        this.name = name;
        this.holderUUID = holder;
        this.balance = balance;
        this.transactions = transactions;
        this.bank = bank;
        this.transferLimit = 1000;
        this.withdrawLimit = 1000;
        this.user = user;
        this.lastTransactionTime = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.todayAmount = 0;
        this.lastWithdrawalTime = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.withdrawTodayAmt = 0;

    }

    // this is to return the currency
    protected Currency getCurrency() {
        return this.currency;
    }

    // this is to set the currency of the account
    protected void setCurrency(Currency c) {
        this.currency = c;
    }

    // this function is used to get the user id of the account
    protected String getUUID() {
        return this.uuid;
    }

    // this funnction is used to get the limit for the transfer for this account
    protected double getTransferLimit() {
        return this.transferLimit;
    }

    // this function is used to get the limit for the withdraw for this account
    protected double getWithdrawLimit() {
        return this.withdrawLimit;
    }


    protected double getTodayAmount() { return this.todayAmount;}

    //
    protected double getWithdrawTodayAmt() { return this.withdrawTodayAmt; };

    //
    protected Date getLastTransactionTime() { return this.getLastTransactionTime();}

    //
    protected Date getLastWithdrawalTime() { return this.getLastWithdrawalTime(); }

    // this is to set the new transfer limit in the settings
    protected void setTransferLimit(double newLimit) {
        this.transferLimit = newLimit;
    }

    // this is to set a new withdraw limit in the settings
    protected void setWithdrawLimit(double newLimit) {
        this.withdrawLimit= newLimit;
    }

    // this is to add the time of the last transaction made
    protected void setLastTransactionTime(Date date) { this.lastTransactionTime = date; }

    // this is to set the time of the last withdrawal made
    protected void setLastWithdrawalTime(Date date) { this.lastWithdrawalTime = date; }

    //
    protected void setTodayAmount(double amount) { this.todayAmount = amount; }

    protected void setWithdrawTodayAmt(double amount) { this.withdrawTodayAmt = amount;}

    // this function is to set the user id of the account
    protected void setUUID(String newUUID) {
        this.uuid = newUUID;
    }

    protected String getName() {
        return this.name;
    }

    // this function is to get the holder of the account
    protected String getHolder() {
        return this.holderUUID;
    }

    // this will return the balance of the account
    protected double getBalance() {
        return this.balance;
    }

    // this will set a new balance to the account
    protected void setBalance(double balance) {
        this.balance = balance;
    }

    // get user will return the user object
    protected User getUser() {
        return this.user;
    }

    // get account will return all the accounts that belongs to the user
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

    // this will take in a data type of double called amount
    // it will check if the amount is valid
    // if it is valid, it will add the amount to the balance
    // if it is not valid, it will throw an exception
    protected boolean deposit(double amount) throws InvalidAmountException, InvalidNoteDepositException {
        if (amount <= 0 || amount == -0 || (BigDecimal.valueOf(amount).scale() > 2)) {
            throw new InvalidAmountException(amount);
        }
//        else if (!(amount%2 == 0 || amount%5 == 0)){
//            throw new InvalidNoteDepositException();
//        }
        boolean checker = false;
        double checkingAmt = amount;
        for (int i = this.getCurrency().getNotesArray().size()-1; i >= 0; i--) {
            checkingAmt = checkingAmt % this.getCurrency().getNotesArray().get(i);
            if (checkingAmt == 0) {
                checker = true;
            }
        }
        if (!checker) {
            checkingAmt = amount;
            for (int i = 0;i < this.getCurrency().getNotesArray().size(); i++) {
                checkingAmt = checkingAmt % this.getCurrency().getNotesArray().get(i);
                if (checkingAmt == 0) {
                    checker = true;
                }
            }
        }
//        for (int i = 0;i < this.getCurrency().getNotesArray().size(); i++) {
//            System.out.println(this.getCurrency().getNotesArray().get(i));
//            if (amount % this.getCurrency().getNotesArray().get(i) == 0) {
//                checker = true;
//            }
//        }
        if (!checker) {
            System.out.println("cant deposit this amt!");
            throw new InvalidNoteDepositException(this.getCurrency().getNotesArray(), this.getCurrency().getSymbolBefore());
        }
        amount = this.getCurrency().unconvert(amount);
        System.out.println("Amount: " + amount);
        System.out.println(amount);
        balance += amount;
        this.modifyBalance(balance);
        addTransaction(this, new Transaction(amount, "Deposit", this.getUUID()));
        return true;
        //this.transactions.add(new Transaction(amount, "Deposit", this.getUUID()));
    }

    // this function will add a new transaction into the transaction object
    protected boolean addTransactionNoDb(Transaction transaction) {
        this.transactions.add(transaction);
        return true;
    }

    // this function will add a new transaction of the account to the database
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

    // this will update the amount and time of transaction to the account of the user in the database
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

    // this function will update the account withdrawal and time of withdrawal to the account of the user in the database
    protected boolean updateAccountWithdrawal(Date newDate, double withdrawTodayAmt) {
        try {
            MongoCollection<Document> accountCollection = this.bank.database.getCollection("accounts");
            Bson filter = Filters.eq("_id", this.getUUID());
            Bson updateOperation = new Document("$set", new Document()
                    .append("withdrawTodayAmt", withdrawTodayAmt)
                    .append("lastWithdrawalTime", newDate));
            accountCollection.updateOne(filter, updateOperation);
            return true;
        } catch (MongoException e) {
            return false;
        }
    }

    // change limit will take in the type of limit and the new limit
    // it will check which type of limit is selected
    // if it is localTransferLimit, it will update the transfer limit
    // if it is localWithdrawLimit, it will update the withdraw limit
    // if it is not either of the two, it will return false
    protected boolean changeLimit(String type, double newLimit) {
        try {
            if (type == "localTransferLimit") {
                this.setTransferLimit(newLimit);
            } else if (type == "localWithdrawLimit") {
                this.setWithdrawLimit(newLimit);
            }  else {
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

    // this will print the transaction history which contains the summary line and the amount
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

    // this will return the transaction history which contains the summary line and the amount
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

    // modify balance will update the balance of the account in the database
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

    // withdraw function will check if the amount is valid
    // if it is valid, it will check if the amount is a multiple of the notes
    // if it is a multiple of the notes, it will withdraw the amount
    // if it is not a multiple of the notes, it will throw an exception

    protected boolean withdraw(double amount) throws InvalidWithdrawAndTransferAmountException, WithdrawLimitException, InvalidNoteWithdrawalException {
        if (amount <= 0 || amount == -0 || (BigDecimal.valueOf(amount).scale() > 2) || this.getCurrency().unconvert(amount) > this.balance) {
            throw new InvalidWithdrawAndTransferAmountException(amount, balance, this.getCurrency());
        }
        boolean checker = false;
        double checkingAmt = amount;
        for (int i = this.getCurrency().getNotesArray().size()-1; i >= 0; i--) {
            checkingAmt = checkingAmt % this.getCurrency().getNotesArray().get(i);
            if (checkingAmt == 0) {
                checker = true;
            }
        }
        if (!checker) {
            checkingAmt = amount;
            for (int i = 0;i < this.getCurrency().getNotesArray().size(); i++) {
                checkingAmt = checkingAmt % this.getCurrency().getNotesArray().get(i);
                if (checkingAmt == 0) {
                    checker = true;
                }
            }
        }
        if (!checker) {
            System.out.println("cant withdraw this amt!");
            throw new InvalidNoteWithdrawalException(this.getCurrency().getNotesArray(), this.getCurrency().getSymbolBefore());
        }
        LocalDate currentDateLocalDate = LocalDate.now();
        Date currentDate = Date.from(currentDateLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        amount = this.getCurrency().unconvert(amount);
        //System.out.println("HERE");
        //System.out.println(amount);
        if ((currentDate.getDay() != this.lastWithdrawalTime.getDay() && currentDate.getMonth() != this.lastWithdrawalTime.getMonth() && currentDate.getYear() != this.lastWithdrawalTime.getYear()) && (amount < this.withdrawLimit)) {
            this.withdrawTodayAmt = amount;
            this.lastWithdrawalTime = currentDate;
            this.updateAccountWithdrawal(currentDate, amount);
        } else if ((currentDate.getDay() != this.lastWithdrawalTime.getDay() && currentDate.getMonth() != this.lastWithdrawalTime.getMonth() && currentDate.getYear() != this.lastWithdrawalTime.getYear()) && (amount > this.withdrawLimit)) {
            this.withdrawTodayAmt = 0;
            this.updateAccountWithdrawal(currentDate, 0);
            if (this.user.getCountry().equals("Japan")) {
                //System.out.println("this");
                throw new WithdrawLimitException(amount, Math.floor(this.getCurrency().convert(this.getWithdrawLimit())), this.getCurrency());
            } else {
                throw new WithdrawLimitException(amount, this.getCurrency().convert(this.getWithdrawLimit()), this.getCurrency());
            }
        }
        else if ((currentDate.getDay() == this.lastWithdrawalTime.getDay() && currentDate.getMonth() == this.lastWithdrawalTime.getMonth() && currentDate.getYear() == this.lastWithdrawalTime.getYear()) && (this.withdrawTodayAmt+amount > this.withdrawLimit)) {
            if (this.user.getCountry().equals("Japan")) {
                //System.out.println("this");
                throw new WithdrawLimitException(amount, Math.floor(this.getCurrency().convert(this.getWithdrawLimit())), this.getCurrency(), Math.floor(this.getCurrency().convert(this.withdrawTodayAmt)));
            } else {
                throw new WithdrawLimitException(amount, this.getCurrency().convert(this.getWithdrawLimit()), this.getCurrency(), this.getCurrency().convert(this.withdrawTodayAmt));
            }
        } else if (amount > this.withdrawLimit) {
            if (this.user.getCountry().equals("Japan")) {
                //System.out.println("this");
                throw new WithdrawLimitException(amount, Math.floor(this.getCurrency().convert(this.getWithdrawLimit())), this.getCurrency(), Math.floor(this.getCurrency().convert(this.withdrawTodayAmt)));
            } else {
                throw new WithdrawLimitException(amount, this.getCurrency().convert(this.getWithdrawLimit()), this.getCurrency(), this.getCurrency().convert(this.withdrawTodayAmt));
            }
        } else {
            this.lastWithdrawalTime = currentDate;
            double newAmount = this.withdrawTodayAmt+amount;
            this.withdrawTodayAmt = newAmount;
            this.updateAccountWithdrawal(currentDate, newAmount);
        }
//        else if (amount%10 != 0){
//            throw new InvalidNoteWithdrawalException();
//        }
//        if (amount > this.getLocalWithdrawLimit()){
//            throw new WithdrawLimitException(amount, this.getLocalWithdrawLimit());
//        }

        balance -= amount;
        this.modifyBalance(balance);
        addTransaction(this, new Transaction(amount, "Withdrawal", this.getUUID()));
        //this.transactions.add(new Transaction(amount, "Withdrawal", this.getUUID()));
        return true;
    }

    // this will return the list of transactions
    protected ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    // this function will throw exceptions if amount is less than or equal to 0
    // or if the amount is greater than the balance
    // if the amount is valid, it will add the amount to the balance
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

    // get other balance would return the balance of the other accounts
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

    // this function will take in 3 inputs which are the account number, memo and amount.
    // it will throw exceptions if the amount is less than or equal to 0, if the amount is greater than the balance
    // or if the amount is greater than the transfer limit

    // if the amount is valid, it will subtract the amount from the balance and add the amount to the other account's balance
    // it will also add the transaction to the transaction list
    // it will return true if the transaction is successful
    // it will return false if the transaction is unsuccessful
    // it will also update the last transaction time
    protected boolean transfer(String accountNumber, String memo, double amount) throws InvalidWithdrawAndTransferAmountException, TransferLimitException
    {
        if (amount <= 0 || amount == -0 || (BigDecimal.valueOf(amount).scale() > 2) || this.getCurrency().unconvert(amount) > this.balance){
            throw new InvalidWithdrawAndTransferAmountException(amount, this.balance, this.getCurrency());
        }
        LocalDate currentDateLocalDate = LocalDate.now();
        Date currentDate = Date.from(currentDateLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        // check if day same, if same see is todayAmount + amount > limit
        //System.out.println(String.valueOf(currentDate.getDay())+ this.lastTransactionTime.getDay());
        amount = this.getCurrency().unconvert(amount);
        if ((currentDate.getDay() != this.lastTransactionTime.getDay() && currentDate.getMonth() != this.lastTransactionTime.getMonth() && currentDate.getYear() != this.lastTransactionTime.getYear()) && (amount < this.transferLimit)) {
            this.todayAmount = amount;
            this.lastTransactionTime = currentDate;
            this.updateAccount(currentDate, amount);
        } else if ((currentDate.getDay() != this.lastTransactionTime.getDay() && currentDate.getMonth() != this.lastTransactionTime.getMonth() && currentDate.getYear() != this.lastTransactionTime.getYear()) && (amount > this.transferLimit)) {
            this.todayAmount = 0;
            this.updateAccount(currentDate, 0);
            if (this.user.getCountry().equals("Japan")) {
                //System.out.println("this");
                throw new TransferLimitException(amount, Math.floor(this.getCurrency().convert(this.getTransferLimit())), this.getCurrency());
            } else {
                throw new TransferLimitException(amount, this.getCurrency().convert(this.getTransferLimit()), this.getCurrency());
            }
        }
        else if ((currentDate.getDay() == this.lastTransactionTime.getDay() && currentDate.getMonth() == this.lastTransactionTime.getMonth() && currentDate.getYear() == this.lastTransactionTime.getYear()) && (this.todayAmount+amount > this.transferLimit)) {
            if (this.user.getCountry().equals("Japan")) {
                //System.out.println("this");
                throw new TransferLimitException(amount, Math.floor(this.getCurrency().convert(this.getTransferLimit())), this.getCurrency(), Math.floor(this.getCurrency().convert(this.todayAmount)));
            } else {
                throw new TransferLimitException(amount, this.getCurrency().convert(this.getTransferLimit()), this.getCurrency(), this.getCurrency().convert(this.todayAmount));
            }
        } else if (amount > this.transferLimit) {
            if (this.user.getCountry().equals("Japan")) {
                //System.out.println("this");
                throw new TransferLimitException(amount, Math.floor(this.getCurrency().convert(this.getTransferLimit())), this.getCurrency(), Math.floor(this.getCurrency().convert(this.todayAmount)));
            } else {
                throw new TransferLimitException(amount, this.getCurrency().convert(this.getTransferLimit()), this.getCurrency(), this.getCurrency().convert(this.todayAmount));
            }
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
