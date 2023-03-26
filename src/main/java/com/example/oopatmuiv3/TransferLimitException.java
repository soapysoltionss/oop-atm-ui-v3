package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class TransferLimitException extends Exception {
    private double amount;
    private double limit;

    private Currency currency;



    public TransferLimitException(double amount, double limit, Currency currency) {
        this.amount = amount; 
        this.limit = limit;
        this.currency = currency;
    }

    public String getMessage() {
        return "Transfer Limit of : "+ this.currency.getSymbolBefore() + " " + Double.toString(this.limit);
    }
}