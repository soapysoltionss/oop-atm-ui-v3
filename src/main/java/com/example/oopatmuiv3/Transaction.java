package com.example.oopatmuiv3;

import java.util.Date;

public class Transaction {
    private double amount;
    private Date timestamp;
    private String memo;
    private String holder;
    
    protected Transaction(double amount, String holder) {
        this.amount = amount;
        this.holder = holder;
        this.timestamp = new Date();
        this.memo = "";
    }

    protected Transaction(double amount, String memo, String holder) {
        this(amount, holder);
        this.memo = memo;
        this.timestamp = new Date();
    }

    protected double getAmount() {
        return amount;
    }

    protected Date getTimeStamp() {
        return timestamp;
    }

    protected void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    protected String getMemo() {
        return memo;
    }

    protected String getHolder() {
        return holder;
    }

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
