package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidWithdrawAndTransferAmountException extends Exception {
    private double amount;
    private double balance;

    public InvalidWithdrawAndTransferAmountException(double amount, double balance) {
        this.amount = amount; 
        this.balance = balance;
    }

    public String getMessage() {
        if (this.amount <= 0 || this.amount == -0) {
            return "Amount must be greater than zero.";
        } else if ((BigDecimal.valueOf(this.amount).scale() > 2)) {
            return "Amount must not have more than 2dp.";
        } else if (this.amount > this.balance) {
            return String.format("Amount must not be greater than\n" + "balance of $%.02f.\n", balance);
        }
        return "Invalid Amount";
    }
}