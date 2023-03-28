package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidLoginException extends Exception {

    // this getMessage method will be called when the user has entered an invalid account ID or PIN
    public String getMessage() {
        return "Invalid Account ID or PIN!";
    }
}