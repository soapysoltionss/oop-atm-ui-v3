package com.example.oopatmuiv3;

public class InvalidNewPinException extends Exception {

    // this getMessage method will be called when the user has entered an invalid new PIN
    public String getMessage() { return "Invalid new PIN, must be 4 digits only. Or the re-entered new pin is different!";}
}
