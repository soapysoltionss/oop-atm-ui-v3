package com.example.oopatmuiv3;

public class InvalidNewPinException extends Exception {

    public String getMessage() { return "Invalid new PIN, must be 4 digits only. Or the re-entered new pin is different!";}
}
