package com.example.oopatmuiv3;

public class Currency {
    private double exchangeRate;
    private String symbolAfter;
    private String symbolBefore;
    private String country;

    protected Currency(String country, String symbolAfter, String symbolBefore, double exchangeRate) {
        this.country = country;
        this.symbolAfter = symbolAfter;
        this.symbolBefore = symbolBefore;
        this.exchangeRate = exchangeRate;
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

    protected double convert(double amount) {
        double theRate = this.getExchangeRate();
        double converted = amount*theRate;
        return converted;
    }

    protected double unconvert(double amount) {
        double theRate = this.getExchangeRate();
        double converted = amount/theRate;
        return converted;
    }
}
