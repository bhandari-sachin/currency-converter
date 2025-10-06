package fi.metropolia.currency_converter.model.config;



import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find database.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/currency_db");
    }

    public static String getUser() {
        return properties.getProperty("db.user", "postgres");
    }

    public static String getPassword() {
        return properties.getProperty("db.password", "postgres");
    }
}
