package com.example.oopatmuiv3;

public class Currency {
    private double exchangeRate;
    private String symbolAfter;
    private String symbolBefore;
    private String country;

    public Currency(String country, String symbolAfter, String symbolBefore, double exchangeRate) {
        this.country = country;
        this.symbolAfter = symbolAfter;
        this.symbolBefore = symbolBefore;
        this.exchangeRate = exchangeRate;
    }

    public String getCountry() {
        return this.country;
    }

    public String getSymbolAfter() {
        return this.symbolAfter;
    }

    public String getSymbolBefore() {
        return this.symbolBefore;
    }

    public double getExchangeRate() {
        return this.exchangeRate;
    }

    public double convert(double amount) {
        double theRate = this.getExchangeRate();
        double converted = amount*theRate;
        return converted;
    }

    public double unconvert(double amount) {
        double theRate = this.getExchangeRate();
        double converted = amount/theRate;
        return converted;
    }
}
