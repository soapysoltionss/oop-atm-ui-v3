package com.example.oopatmuiv3;

import java.math.BigDecimal;

public class InvalidLoginException extends Exception {


    public String getMessage() {
        return "Invalid Account ID or PIN!";
    }
}