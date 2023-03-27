package com.example.oopatmuiv3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TransferLimitException extends Exception {
    private double amount;
    private double limit;
    private double todayAmt=-1;

    private Currency currency;
    DecimalFormat df = new DecimalFormat("0.00");

    public TransferLimitException(double amount, double limit, Currency currency) {
        this.amount = amount; 
        this.limit = limit;
        this.currency = currency;
    }

    public TransferLimitException(double amount, double limit, Currency currency, double todayAmt) {
        this.amount = amount;
        this.limit = limit;
        this.currency = currency;
        this.todayAmt = todayAmt;
    }

    public String getMessage() {
        df.setRoundingMode(RoundingMode.DOWN);
        if (todayAmt!=-1){
            return "Transfer Limit of : "+ this.currency.getSymbolBefore() + " " + Double.toString(this.limit) + "\nCan only transfer: "+ this.currency.getSymbolBefore()+df.format(this.limit - this.todayAmt);
        }
        return "Transfer Limit of : "+ this.currency.getSymbolBefore() + " " + Double.toString(this.limit);
    }
}