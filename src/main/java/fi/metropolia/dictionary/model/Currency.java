package fi.metropolia.dictionary.model;

public class Currency {
    private String abbreviation;
    private String name;
    private double rateToUSD;

    public Currency(String abbreviation, String name, double rateToUSD) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.rateToUSD = rateToUSD;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public double getRateToUSD() {
        return rateToUSD;
    }

    public void setRateToUSD(double rateToUSD) {
        this.rateToUSD = rateToUSD;
    }

    @Override
    public String toString() {
        return abbreviation + " - " + name;
    }
}
