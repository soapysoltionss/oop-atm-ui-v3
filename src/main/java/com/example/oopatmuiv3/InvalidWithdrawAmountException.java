package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidWithdrawAmountException extends Exception {
    private double amount;
    private double balance;

    public InvalidWithdrawAmountException(double amount, double balance) { 
        this.amount = amount; 
        this.balance = balance;
    }

    public void errorMessage() {
        if(amount <= 0){
            System.out.println("Amount must be greater than zero.");
        } else if (amount > balance) {
            System.out.printf("Amount must not be greater than\n" + "balance of $%.02f.\n", balance);
        } else if ((BigDecimal.valueOf(amount).scale() > 2)){
            System.out.println("Amount must not have more than 2dp.");
        } else if (amount == -0) {
            System.out.printf("Amount to withdraw can't be 0 and must be greater than 0");
        }
    }
}
