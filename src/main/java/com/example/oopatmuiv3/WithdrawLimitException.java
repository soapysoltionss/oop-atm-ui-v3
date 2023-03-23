package com.example.oopatmuiv3;

public class WithdrawLimitException extends Exception {
    private double amount;
    private double limit;

    public WithdrawLimitException(double amount, double limit) {
        this.amount = amount; 
        this.limit = limit;
    }

    public String getMessage() {
        return "Withdraw Limit of "+Double.toString(this.limit);
    }
}