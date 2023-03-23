package com.example.oopatmuiv3;

public class InvalidNoteWithdrawalException extends Exception {

    public String getMessage() {
        return "Only $10, $50 & $100 notes available!";
    }
}