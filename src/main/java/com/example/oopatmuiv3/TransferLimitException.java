package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class TransferLimitException extends Exception {
    private double amount;
    private double limit;

    public TransferLimitException(double amount, double limit) {
        this.amount = amount; 
        this.limit = limit;
    }

    public String getMessage() {
        return "Transfer Limit of "+Double.toString(this.limit);
    }
}