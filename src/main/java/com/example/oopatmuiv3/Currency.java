package com.example.oopatmuiv3;

import java.util.ArrayList;
import java.util.List;

public class Currency {
    private double exchangeRate;
    private String symbolAfter;
    private String symbolBefore;
    private String country;

    private List<Integer> notesArray;

    private String error;

    protected Currency(String country, String symbolAfter, String symbolBefore, double exchangeRate, List<Integer> notesArray, String error) {
        this.country = country;
        this.symbolAfter = symbolAfter;
        this.symbolBefore = symbolBefore;
        this.exchangeRate = exchangeRate;
        this.notesArray = notesArray;
        this.error = error;
    }

    protected String getCountry() {
        return this.country;
    }

    protected String getSymbolAfter() {
        return this.symbolAfter;
    }

    protected String getSymbolBefore() {
        return this.symbolBefore;
    }

    protected double getExchangeRate() {
        return this.exchangeRate;
    }

    protected List<Integer> getNotesArray() { return this.notesArray; }

    // this convert function is to convert the current amount the user has by
    // taking the exchange rate multiply by the amount the user has and return the new value
    protected double convert(double amount) {
        double theRate = this.getExchangeRate();
        double converted = amount*theRate;
        return converted;
    }

    // this unconvert function is to convert the current amount the user has by
    // taking the amount the user has divided by the exchange rate and return the new value
    protected double unconvert(double amount) {
        double theRate = this.getExchangeRate();
        double converted = amount/theRate;
        return converted;
    }
}
