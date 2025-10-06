package fi.metropolia.currency_converter.model;

import jakarta.persistence.*;

/**
 * JPA Entity class representing a currency with its exchange rate to USD.
 * Matches the database schema exactly.
 */
@Entity
@Table(name = "currency")
public class Currency {
    @Id
    @Column(name = "abbreviation", columnDefinition = "CHAR(3)")
    private String abbreviation;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "rate_to_usd", nullable = false)
    private double rateToUSD;

    /**
     * Default constructor (required by JPA).
     */
    public Currency() {
    }

    /**
     * Constructor with parameters.
     */
    public Currency(String abbreviation, String name, double rateToUSD) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.rateToUSD = rateToUSD;
    }

    // Getters and setters
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Currency currency = (Currency) obj;
        return abbreviation != null && abbreviation.equals(currency.abbreviation);
    }

    @Override
    public int hashCode() {
        return abbreviation != null ? abbreviation.hashCode() : 0;
    }
}