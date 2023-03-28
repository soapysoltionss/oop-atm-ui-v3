package com.example.oopatmuiv3;

import java.util.Date;

// Declare variables for transaction
public class Transaction {
    private double amount;
    private Date timestamp;
    private String memo;
    private String holder;

    // constructor for Transaction class that takes in amount, holder, timestamp and memo
    protected Transaction(double amount, String holder) {
        this.amount = amount;
        this.holder = holder;
        this.timestamp = new Date();
        this.memo = "";
    }
    // takes the transaction with amount, holder and memo when a transaction is made
    protected Transaction(double amount, String memo, String holder) {
        this(amount, holder);
        this.memo = memo;
        this.timestamp = new Date();
    }

    // get the amount of the transaction
    protected double getAmount() {
        return amount;
    }

    // get the timestamp of the transaction
    protected Date getTimeStamp() {
        return timestamp;
    }

    // set the timestamp of the transaction
    protected void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // get the memo of the transaction
    protected String getMemo() {
        return memo;
    }

    // get the holder uuid of the transaction
    protected String getHolder() {
        return holder;
    }

    // get the summary line of the transaction
    protected String getSummaryLine() throws Exception{
        try {
            if (this.amount >= 0) {
                return String.format("%s : $%.02f : %s", this.timestamp.toString(), this.amount, this.memo);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        // if(this.amount >= 0){
        //     return String.format("%s : $%.2f : %s", this.timestamp.toString(), this.amount, this.memo);
        // }else{
        //     return String.format("%s : $%.2f : %s", this.timestamp.toString(), -this.amount, this.memo);
        // }
        return String.format("%s : $%.02f : %s", this.timestamp.toString(), this.amount, this.memo);
    }
}
