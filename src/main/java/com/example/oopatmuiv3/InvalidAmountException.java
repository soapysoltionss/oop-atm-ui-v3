package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidAmountException extends Exception {
    private double amount;
    public InvalidAmountException(double amount) { 
        this.amount = amount; 
    }

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