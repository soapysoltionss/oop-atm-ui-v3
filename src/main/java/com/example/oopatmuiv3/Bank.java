package com.example.oopatmuiv3;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.security.NoSuchAlgorithmException;
import java.util.*;


public class Bank {
    //Declare variables
    private String name;
    private ArrayList<User> users;
    private ArrayList<Account> accounts;
    private ArrayList<Currency> currencies;
    MongoDatabase database;
    private MongoClient mongoClient;
    private MongoCollection<Document> usersCollection;
    private MongoCollection<Document> accountsCollection;
    private MongoCollection<Document> transactionsCollection;
    private MongoCollection<Document> currencyCollection;

    // this bank is to create a new connection to the mongodb database
    protected Bank(String name) {
        this.name = name;
        //this.accounts = new ArrayList<>();
        ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:admin@oop.ulsonro.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings
                .builder().applyConnectionString(connectionString)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .build();
        // these variables are used to allow us to get, insert, update and delete data from the collections in the database
        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase("bank");
        this.usersCollection = database.getCollection("users");
        this.accountsCollection = database.getCollection("accounts");
        this.transactionsCollection = database.getCollection("transactions");
        this.currencyCollection = database.getCollection("currency");
        this.currencies = this.getCurrencies();
        this.users = this.getUsers();
        this.accounts = new ArrayList<>();


    }

    // this get new user uuid is to create a new user id for the user
    // the user id must be unique and it must be 10 digits long
    protected String getNewUserUUID() {
        String uuid;
        Random rng = new Random();
        int len = 10;
        boolean nonUnique = false;
        ArrayList<String> uuidList = new ArrayList<>();
        MongoCollection<Document> uuidCollection = this.database.getCollection("uuids");
        for (Document doc:uuidCollection.find()) {
            uuidList.add(doc.getString("uuid"));
        }
        do {
            uuid = "";
            for (int c = 0; c < len; c++) {
                uuid += ((Integer)rng.nextInt(10)).toString();
            }
            for (String u:uuidList) {
                if (u.compareTo(uuid) == 0) {
                    continue;
                }
            }
            nonUnique = true;
            break;
        } while (nonUnique == false);
        return uuid;
    }

    // this is to get the new accounts id for the user
    // the account id must be unique and it must be 10 digits long
    protected String getNewAccountUUID() {
        String uuid;
        Random rng = new Random();
        int len = 10;
        boolean nonUnique = false;
        ArrayList<String> uuidList = new ArrayList<>();
        MongoCollection<Document> uuidCollection = this.database.getCollection("uuids");
        for (Document doc:uuidCollection.find()) {
            uuidList.add(doc.getString("uuid"));
        }
        do {
            uuid = "";
            for (int c = 0; c < len; c++) {
                uuid += ((Integer)rng.nextInt(10)).toString();
            }
            for (String u:uuidList) {
                if (u.compareTo(uuid) == 0) {
                    continue;
                }
            }
            nonUnique = true;
            break;
        } while (nonUnique == false);
        return uuid;
    }

    // this is update the users in the bank from the database
    protected void refreshUsers() {
        this.users = this.getUsers();
    }

    // add a new user to the bank
    protected void addUser(User user) {
        users.add(user);
    }

    // number of users in the bank
    protected int countUsers() {
        return this.users.size();
    }

    // this function is used to get the name of the bank
    protected String getName() {
        return this.name;
    }

    // this is to print the first name of the user
    protected void printInfo() {
        for (User user : users) {
            System.out.println(user.getFirstName() + " " + user.getLastName() + " : " + user.getUUID());
        }
    }

    // user login function is used to check if the user id and pin is correct
    // if it is correct, it will return the user
    // else it will throw the invalid login exception
    protected User userLogin(String uuid, String pin) throws InvalidLoginException {
        for (User u:this.users) {
            if (u.getUUID().compareTo(uuid) == 0 && u.validatePin(pin)) {
                return u;
            }
        }
        throw new InvalidLoginException();
    }


    // this function is to add a new user to the database using:
    // the first name, last name, pin and country
    protected User addUser(String firstName, String lastName, String pin, String country) throws NoSuchAlgorithmException {
        User newUser = new User(firstName, lastName, User.hashPin(pin), this, country);
        this.users.add(newUser);

        MongoCollection<Document> usersCollection = this.database.getCollection("users");
        Document userDocument = new Document("_id", newUser.getUUID())
                .append("firstName", newUser.getFirstName())
                .append("lastName", newUser.getLastName())
                .append("pinHash", newUser.getPinHash().toString())
                .append("country", newUser.getCountry());
        usersCollection.insertOne(userDocument);
        MongoCollection<Document> uuidsCollection = this.database.getCollection("uuids");
        Document newDoc = new Document().append("uuid", newUser.getUUID());
        uuidsCollection.insertOne(newDoc);
        return newUser;
    }

    // get currencies is to get the currencies such as
    // their country code, country, exchange rate and money symbols from the database:
    // for example SGD, Singapore, 1.0, $
    protected ArrayList<Currency> getCurrencies() {
        ArrayList<Currency> currencyList = new ArrayList<>();
        for (Document currencyDoc : currencyCollection.find()) {
            String country = currencyDoc.getString("country");
            String symbolAfter = currencyDoc.getString("symbolAfter");
            String symbolBefore = currencyDoc.getString("symbolBefore");
            double exchangeRate = currencyDoc.getDouble("exchangeRate");
            List<Integer> bsonDoubles = (List<Integer>) currencyDoc.get("notesArray");
            //int[] d = bsonDoubles.stream().mapToInt(Integer::intValue).toArray();
            //System.out.println("HI IM HERE");
            //System.out.println(bsonDoubles);
            String error = currencyDoc.getString("error");
            Currency newCurrency = new Currency(country, symbolAfter, symbolBefore, exchangeRate, bsonDoubles, error);
            currencyList.add(newCurrency);
        }
        return currencyList;
    }

    // get users method will get the user details from the database
    // for example, using the first name, last name, pin and country
    // we will search through the database for that ID
    // once we find the ID, we will get the user details like
    // balance, withdrawal limit, transfer limit and deposit limit
    // and return the user details from the database
    protected ArrayList<User> getUsers() {
        ArrayList<User> userList = new ArrayList<>();
        this.currencies = getCurrencies();
        for (Document doc : usersCollection.find()) {
            String UserUuid = doc.get("_id").toString();
            //System.out.println(uuid);
            String firstName = doc.getString("firstName");
            String lastName = doc.getString("lastName");
            String pinHash = doc.getString("pinHash");
            String country = doc.getString("country");
            User user = new User(firstName, lastName, pinHash, this, country);
            //System.out.println(user.getFirstName());
            user.setUUID(UserUuid);
            ArrayList<Account> accounts = new ArrayList<Account>();
            for (Document accountDoc: accountsCollection.find(Filters.eq("holderUUID", UserUuid))) {
                String accountUUID = accountDoc.get("_id").toString();
                double accountBalance = accountDoc.getDouble("balance");
                String accountName = accountDoc.getString("name");
                double localTransferLimit = accountDoc.getDouble("localTransferLimit");
                //double overseasTransferLimit = accountDoc.getDouble("overseasTransferLimit");
                double localWithdrawLimit = accountDoc.getDouble("localWithdrawLimit");
                //double overseasWithdrawLimit = accountDoc.getDouble("overseasWithdrawLimit");
                double todayAmount = accountDoc.getDouble("todayAmount");
                Date lastTransactionTime = accountDoc.getDate("lastTransactionTime");
                double withdrawTodayAmt = accountDoc.getDouble("withdrawTodayAmt");
                Date lastWithdrawalTime = accountDoc.getDate("lastWithdrawalTime");
                //System.out.println(lastTransactionTime);
                ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                for (Document transactionDoc : transactionsCollection.find(Filters.eq("holder", accountUUID))) {
                    double transactionAmount = transactionDoc.getDouble("amount");
                    String transactionMemo = transactionDoc.getString("memo");
                    Date transactionTimestamp = transactionDoc.get("timestamp", Date.class);
                    Transaction transaction = new Transaction(transactionAmount, transactionMemo, accountUUID);
                    transaction.setTimestamp(transactionTimestamp);
                    transactions.add(transaction);
                }
                Account account = new Account(accountName, UserUuid, accountUUID, transactions, this, accountBalance, user);
                for (Currency currency: currencies) {
                    if (country.equalsIgnoreCase(currency.getCountry())) {
                        account.setCurrency(currency);
                    }
                }
                account.setTodayAmount(todayAmount);
                account.setLastTransactionTime(lastTransactionTime);
                account.setWithdrawTodayAmt(withdrawTodayAmt);
                account.setLastWithdrawalTime(lastWithdrawalTime);
                account.setTransferLimit(localTransferLimit);
                account.setWithdrawLimit(localWithdrawLimit);
                //account.setOverseasTransferLimit(overseasTransferLimit);
                //account.setOverseasWithdrawLimit(overseasWithdrawLimit);
                //System.out.println(account.getName());
                accounts.add(account);
            }
            user.setAccounts(accounts);
            //System.out.println(user.getPinHash());
            userList.add(user);
        }

        return userList;
    }
}
