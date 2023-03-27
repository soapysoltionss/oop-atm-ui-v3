package com.example.oopatmuiv3;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class WithdrawLimitException extends Exception {
    private double amount;
    private double limit;
    private double todayAmt;
    private Currency currency;
    DecimalFormat df = new DecimalFormat("0.00");

    public WithdrawLimitException(double amount, double limit, Currency currency) {
        this.amount = amount; 
        this.limit = limit;
        this.currency =currency;
    }
    public WithdrawLimitException(double amount, double limit, Currency currency, double todayAmt) {
        this.amount = amount;
        this.limit = limit;
        this.currency =currency;
        this.todayAmt = todayAmt;
    }

    public String getMessage() {
        df.setRoundingMode(RoundingMode.DOWN);
        if (todayAmt!=-1){
            return "Withdraw Limit of : "+ this.currency.getSymbolBefore() + " " + Double.toString(this.limit) + "\nCan only withdraw: "+this.currency.getSymbolBefore()+df.format(this.limit - this.todayAmt);
        }
        return "Withdraw Limit of : "+ this.currency.getSymbolBefore() + " " + Double.toString(this.limit);
    }
}