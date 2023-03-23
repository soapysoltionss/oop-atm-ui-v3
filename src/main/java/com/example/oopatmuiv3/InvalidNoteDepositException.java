package com.example.oopatmuiv3;

public class InvalidNoteDepositException extends Exception {

    public String getMessage() {
        return "Only deposit $2, $5, $10, $50 & $100!";
    }
}