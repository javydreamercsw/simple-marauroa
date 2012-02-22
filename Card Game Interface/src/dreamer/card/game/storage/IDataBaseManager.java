/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage;

import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IDataBaseManager {

    /**
     * @return the Entity Manager Factory
     */
    public EntityManagerFactory getEntityManagerFactory();

    /**
     * @return the Entity Manager
     */
    public EntityManager getEntityManager();
    
    public void nativeQuery(String query) throws Exception;

    public List<Object> namedQuery(String query) throws Exception;

    public List<Object> namedQuery(String query, HashMap<String, Object> parameters) throws Exception;
    
    public void close();
}
