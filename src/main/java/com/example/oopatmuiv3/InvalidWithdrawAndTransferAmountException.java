package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidWithdrawAndTransferAmountException extends Exception {
    private double amount;
    private double balance;

    private Currency currency;

    // this is the constructor that will be invoked when the exception is thrown using 3 inputs
    public InvalidWithdrawAndTransferAmountException(double amount, double balance, Currency currency) {
        this.amount = amount; 
        this.balance = balance;
        this.currency = currency;
    }

    // getMessage method will be called when the user has input an invalid amount such as
    // negative amount, amount with more than 2dp, amount greater than balance
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