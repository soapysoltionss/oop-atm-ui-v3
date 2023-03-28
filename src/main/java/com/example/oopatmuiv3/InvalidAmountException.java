package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidAmountException extends Exception {
    private double amount;

    // this is the constructor that will be invoked when the exception is thrown using 1 input
    public InvalidAmountException(double amount) {
        this.amount = amount; 
    }

    // getMessage method will be called when the user has input an invalid amount such as
    // negative amount, amount with more than 2dp and less than 0
    public String getMessage() {
        if (this.amount <= 0 || this.amount == -0) {
            return "Amount must be greater than zero.";
        } else if ((BigDecimal.valueOf(this.amount).scale() > 2)) {
            return "Amount must not have more than 2dp.";
        }
        else{
            return "Invalid Amount.";
        }
    }

}