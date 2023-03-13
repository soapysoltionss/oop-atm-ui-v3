package com.example.oopatmuiv3;

import java.util.Date;

public class Transaction {
    private double amount;
    private Date timestamp;
    private String memo;
    private String holder;
    
    public Transaction(double amount, String holder) {
        this.amount = amount;
        this.holder = holder;
        this.timestamp = new Date();
        this.memo = "";
    }

    public Transaction(double amount, String memo, String holder) {
        this(amount, holder);
        this.memo = memo;
        this.timestamp = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public Date getTimeStamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMemo() {
        return memo;
    }

    public String getHolder() {
        return holder;
    }

    public String getSummaryLine(){

        if(this.amount >= 0){
            return String.format("%s : $%.2f : %s", this.timestamp.toString(), this.amount, this.memo);
        }else{
            return String.format("%s : $%.2f : %s", this.timestamp.toString(), -this.amount, this.memo);
        }
    }
}
