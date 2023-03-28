package com.example.oopatmuiv3;

import java.util.List;

public class InvalidNoteWithdrawalException extends Exception {

    private List<Integer> notesArray;
    private String startSymbol;

    // this is the constructor that will be invoked when the exception is thrown using 2 inputs
    public InvalidNoteWithdrawalException(List<Integer> notesArray, String startSymbol) {
        this.notesArray = notesArray;
        this.startSymbol = startSymbol;

    }

    // get message method will be called when the user has input an invalid note to withdraw
    public String getMessage() {
        StringBuilder msg = new StringBuilder("Only accepts: ");
        for (int i = 0;i < this.notesArray.size(); i++) {
            msg.append(this.startSymbol).append(" ").append(this.notesArray.get(i));
            if (i != this.notesArray.size()-1) {
                msg.append(", ");
            } else {
                msg.append(" for withdrawal.");
            }
        }
        return msg.toString();



    }
}