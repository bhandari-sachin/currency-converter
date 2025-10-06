package fi.metropolia.currency_converter.dao;

import fi.metropolia.currency_converter.model.Currency;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

/**
 * Data Access Object for Currency entities using JPA.
 * Handles all database operations related to currencies.
 * Uses EntityManager per request pattern.
 */
public class CurrencyDAO {
    private static EntityManagerFactory emf;

    public CurrencyDAO() {
        if (emf == null) {
            synchronized (CurrencyDAO.class) {
                if (emf == null) {
                    try {
                        emf = Persistence.createEntityManagerFactory("currency_pu");
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create EntityManagerFactory: " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    private <T> T executeWithTransaction(DAOOperation<T> operation) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            T result = operation.execute(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    private <T> T executeReadOnly(DAOOperation<T> operation) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            return operation.execute(em);
        } catch (Exception e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    public double getExchangeRate(String abbreviation) {
        return executeReadOnly(em -> {
            Currency currency = em.find(Currency.class, abbreviation);
            if (currency == null) {
                throw new RuntimeException("Currency not found: " + abbreviation);
            }
            return currency.getRateToUSD();
        });
    }

    public List<Currency> getAllCurrencies() {
        return executeReadOnly(em ->
                em.createQuery("SELECT c FROM Currency c ORDER BY c.abbreviation", Currency.class)
                        .getResultList()
        );
    }

    public void updateCurrencyRate(String abbreviation, double newRate) {
        executeWithTransaction(em -> {
            Currency currency = em.find(Currency.class, abbreviation);
            if (currency == null) {
                throw new RuntimeException("Currency not found: " + abbreviation);
            }
            currency.setRateToUSD(newRate);
            return null;
        });
    }

    public void insertCurrency(Currency currency) {
        executeWithTransaction(em -> {
            if (em.find(Currency.class, currency.getAbbreviation()) != null) {
                throw new RuntimeException("Currency " + currency.getAbbreviation() + " already exists");
            }
            em.persist(currency);
            return null;
        });
    }

    public boolean currencyExists(String abbreviation) {
        return executeReadOnly(em -> em.find(Currency.class, abbreviation) != null);
    }

    @FunctionalInterface
    private interface DAOOperation<T> {
        T execute(EntityManager em);
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
