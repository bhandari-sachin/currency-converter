package fi.metropolia.currency_converter.dao;

import fi.metropolia.currency_converter.model.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Data Access Object for Transaction entities using JPA.
 * Handles all database operations related to transactions.
 */
public class TransactionDAO {
    private final EntityManagerFactory emf;

    /**
     * Constructor that accepts an EntityManagerFactory.
     * This allows sharing the same factory across DAOs.
     */
    public TransactionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Stores a new transaction in the database.
     * The transaction ID will be generated automatically by the database.
     *
     * @param transaction The transaction to store
     * @throws RuntimeException if the operation fails
     */
    public void saveTransaction(Transaction transaction) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(transaction);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save transaction: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}