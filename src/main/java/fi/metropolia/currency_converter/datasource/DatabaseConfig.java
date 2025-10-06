package fi.metropolia.currency_converter.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for database connection settings.
 * Loads properties from database.properties file.
 */
public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new RuntimeException("Unable to find database.properties file in resources");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the database URL.
     * @return Database URL (e.g., jdbc:postgresql://localhost:5432/currency_db)
     */
    public static String getUrl() {
        return properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/currency_db");
    }

    /**
     * Gets the database username.
     * @return Database username
     */
    public static String getUser() {
        return properties.getProperty("db.user", "postgres");
    }

    /**
     * Gets the database password.
     * @return Database password
     */
    public static String getPassword() {
        return properties.getProperty("db.password", "postgres");
    }

    /**
     * Tests if the configuration is loaded successfully.
     * @return true if configuration is available
     */
    public static boolean isConfigured() {
        return !properties.isEmpty();
    }
}