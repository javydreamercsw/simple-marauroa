package simple.server.application.db;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface DAO {
    /**
     * Register yourself in DAORegister.
     */
    void register();
    /**
     * Perform any initialization needed. Called after the database is initialized.
     */
    void init();
}
