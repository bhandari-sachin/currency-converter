package fi.metropolia.currency_converter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity class representing a currency conversion transaction.
 * Each transaction records a conversion from one currency to another.
 */
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "source_currency", referencedColumnName = "abbreviation", nullable = false)
    private Currency sourceCurrency;

    @ManyToOne
    @JoinColumn(name = "target_currency", referencedColumnName = "abbreviation", nullable = false)
    private Currency targetCurrency;

    @Column(name = "source_amount", nullable = false)
    private double sourceAmount;

    @Column(name = "target_amount", nullable = false)
    private double targetAmount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    /**
     * Default constructor (required by JPA).
     */
    public Transaction() {
    }

    /**
     * Constructor for creating a new transaction.
     * Transaction date is set automatically to current time.
     */
    public Transaction(Currency sourceCurrency, Currency targetCurrency,
                       double sourceAmount, double targetAmount) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.sourceAmount = sourceAmount;
        this.targetAmount = targetAmount;
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Currency getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(Currency sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getSourceAmount() {
        return sourceAmount;
    }

    public void setSourceAmount(double sourceAmount) {
        this.sourceAmount = sourceAmount;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return String.format("Transaction[id=%d, %.2f %s -> %.2f %s, date=%s]",
                transactionId, sourceAmount, sourceCurrency.getAbbreviation(),
                targetAmount, targetCurrency.getAbbreviation(), transactionDate);
    }
}