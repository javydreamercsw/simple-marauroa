package simple.server.application.db;

import java.sql.SQLException;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface IDatabase {

    /**
     * Initialize database.
     *
     * @throws SQLException
     */
    void initialize() throws SQLException;

    /**
     * Method to check if the database already got initialized.
     *
     * @return true if initialized, false otherwise.
     */
    boolean isInitialized();

    /**
     * Disable Marauroa's default initialization. This shouldn't be used unless
     * the Database system is rewritten (i.e. JPA)
     *
     * @param disable True if disabled, false by default;
     */
    void setDisableMarauroaInitialization(boolean disable);

    /**
     * Check if Marauroa's default initialization is disabled.
     *
     * @return true if disabled, false otherwise.
     */
    boolean isMarauroaInitializationDisabled();
}
