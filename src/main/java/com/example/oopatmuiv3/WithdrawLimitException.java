package com.example.oopatmuiv3;

public class WithdrawLimitException extends Exception {
    private double amount;
    private double limit;

    private Currency currency;

    public WithdrawLimitException(double amount, double limit, Currency currency) {
        this.amount = amount; 
        this.limit = limit;
        this.currency =currency;
    }

    public String getMessage() {
        return "Withdraw Limit of : "+ this.currency.getSymbolBefore() + " " + Double.toString(this.limit);
    }
}