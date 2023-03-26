package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidWithdrawAndTransferAmountException extends Exception {
    private double amount;
    private double balance;

    private Currency currency;

    public InvalidWithdrawAndTransferAmountException(double amount, double balance, Currency currency) {
        this.amount = amount; 
        this.balance = balance;
        this.currency = currency;
    }

    public String getMessage() {
        if (this.amount <= 0 || this.amount == -0) {
            return "Amount must be greater than zero.";
        } else if ((BigDecimal.valueOf(this.amount).scale() > 2)) {
            return "Amount must not have more than 2dp.";
        } else if (this.amount > this.balance) {
            return String.format("Amount must not be greater than\n" + "balance of " + this.currency.getSymbolBefore() + "%.02f.\n", this.currency.convert(balance));
        }
        return "Invalid Amount";
    }
}