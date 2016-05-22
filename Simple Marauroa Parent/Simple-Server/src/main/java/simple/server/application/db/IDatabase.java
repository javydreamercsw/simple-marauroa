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
}
