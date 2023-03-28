package com.example.oopatmuiv3;

public class InvalidOldPinException extends Exception {

    // this getMessage method will be called when the user has entered an invalid old PIN
    public String getMessage() { return "Invalid old PIN entered.";}
}


