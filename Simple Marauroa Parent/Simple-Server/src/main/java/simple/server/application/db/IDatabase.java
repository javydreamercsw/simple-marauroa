package simple.server.application.db;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import marauroa.server.db.DatabaseConnectionException;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface IDatabase {

    /**
     * Initialize database.
     *
     * @throws DatabaseConnectionException
     */
    void initialize() throws DatabaseConnectionException;

    /**
     * Method to check if the database already got initialized.
     *
     * @return true if initialized, false otherwise.
     */
    boolean isInitialized();

    /**
     * Set the persistence unit for this database.
     *
     * @param pu new persistence unit name.
     * @throws Exception if something goes wrong.
     */
    public void setPersistenceUnitName(String pu) throws Exception;

    /**
     * Get the persistence unit for this database.
     *
     * @return persistence unit name
     */
    public String getPersistenceUnitName();

    /**
     * Get Entity Manager Factory
     *
     * @return Entity Manager Factory
     * @throws Exception if something goes wrong.
     */
    public EntityManagerFactory getEntityManagerFactory() throws Exception;

    /**
     * Execute a query.
     *
     * @param query named query
     * @param parameters query parameters
     * @return results as a list
     */
    public List<Object> namedQuery(String query,
            Map<String, Object> parameters);

    /**
     * Execute a query.
     *
     * @param query named query
     * @return results as a list
     */
    public List<Object> namedQuery(String query);

    /**
     * Execute a query.
     *
     * @param query query to execute.
     * @param parameters query parameters
     * @return results as a list
     */
    public List<Object> createdQuery(String query,
            Map<String, Object> parameters);

    /**
     * Execute a native query.
     *
     * @param query Query to be executed
     * @throws Exception if something goes wrong.
     */
    public void nativeQuery(String query) throws Exception;
}
