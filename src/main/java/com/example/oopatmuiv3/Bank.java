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
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Bank {
    private String name;
    private ArrayList<User> users;
    private ArrayList<Account> accounts;
    //private ArrayList<Account> accounts;
    MongoDatabase database;
    private MongoClient mongoClient;
    private MongoCollection<Document> usersCollection;
    private MongoCollection<Document> accountsCollection;
    private MongoCollection<Document> transactionsCollection;


    public Bank(String name) {
        this.name = name;
        //this.accounts = new ArrayList<>();
        ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:admin@oop.ulsonro.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings
            .builder().applyConnectionString(connectionString)
            .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
            .build();
        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase("bank");
        this.usersCollection = database.getCollection("users");
        this.accountsCollection = database.getCollection("accounts");
        this.transactionsCollection = database.getCollection("transactions");
        this.users = this.getUsers();
        this.accounts = new ArrayList<>();
    }

    public String getNewUserUUID() {
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

    public String getNewAccountUUID() {
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

    public void refreshUsers() {
        this.users = this.getUsers();
    }
    
    public void addUser(User user) {
        users.add(user);
    }

    public int countUsers() {
        return this.users.size();
    }

    public String getName() {
        return this.name;
    }



    public void printInfo() {
        for (User user : users) {
            System.out.println(user.getFirstName());
        }
    }

    public User userLogin(String uuid, String pin) {
        for (User u:this.users) {
            if (u.getUUID().compareTo(uuid) == 0 && u.validatePin(pin)) {
                return u;
            }
        }
        return null;
    }


    public User addUser(String firstName, String lastName, String pin, String country) throws NoSuchAlgorithmException {
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

    public ArrayList<User> getUsers() {
        ArrayList<User> userList = new ArrayList<>();
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
                ArrayList<Transaction> transactions = new ArrayList<Transaction>();
                for (Document transactionDoc : transactionsCollection.find(Filters.eq("holder", accountUUID))) {
                    double transactionAmount = transactionDoc.getDouble("amount");
                    String transactionMemo = transactionDoc.getString("memo");
                    Date transactionTimestamp = transactionDoc.get("timestamp", Date.class);
                    Transaction transaction = new Transaction(transactionAmount, transactionMemo, accountUUID);
                    transaction.setTimestamp(transactionTimestamp);
                    transactions.add(transaction);
                }
                Account account = new Account(accountName, UserUuid, accountUUID, transactions, this, accountBalance);
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
