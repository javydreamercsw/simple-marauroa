package simple.server.application.db;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.openide.util.Exceptions;
import simple.common.SimpleException;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public abstract class AbstractDatabase implements IDatabase {

    protected static EntityManagerFactory emf;
    private String puName;
    private static final Logger LOG
            = Logger.getLogger(AbstractDatabase.class.getName());

    @Override
    public void setPersistenceUnitName(String pu) throws Exception {
        puName = pu;
        LOG.log(Level.INFO, "Changed persistence unit name to: {0}",
                getPersistenceUnitName());
        //Set it to null so it's recreated with new Persistence Unit
        //next time is requested.
        emf = null;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() throws Exception {
        if (emf == null) {
            LOG.log(Level.WARNING, "Manually specified connection parameters. "
                    + "Using pre-defined persistence unit: {0}",
                    getPersistenceUnitName());
            emf = Persistence.createEntityManagerFactory(
                    getPersistenceUnitName());
        }
        return emf;
    }

    @Override
    public List<Object> namedQuery(String query) {
        try {
            return protectedNamedQuery(query, null, false);
        } catch (SimpleException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public List<Object> namedQuery(String query,
            Map<String, Object> parameters) {
        try {
            return protectedNamedQuery(query, parameters, false);
        } catch (SimpleException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public EntityTransaction getTransaction() throws SimpleException {
        return getEntityManager().getTransaction();
    }

    protected EntityManager getEntityManager() throws SimpleException {
        EntityManager em = null;
        try {
            em = getEntityManagerFactory().createEntityManager();
        } catch (SimpleException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return em;
    }

    @SuppressWarnings(value = "unchecked")
    protected List<Object> protectedNamedQuery(String query,
            Map<String, Object> parameters, boolean locked)
            throws SimpleException {
        Query q;
        getTransaction().begin();
        q = getEntityManager().createNamedQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries
                    = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        List result = q.getResultList();
        if (getTransaction().isActive()) {
            getTransaction().commit();
        }
        return result;
    }

    public List<Object> createdQuery(String query)
            throws SimpleException {
        return createdQuery(query, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> createdQuery(String query,
            Map<String, Object> parameters) {
        try {
            return protectedCreatedQuery(query, parameters, false);
        } catch (SimpleException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public List<Object> protectedCreatedQuery(String query,
            Map<String, Object> parameters, boolean locked)
            throws SimpleException {
        Query q;
        getTransaction().begin();
        q = getEntityManager().createQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries
                    = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        List result = q.getResultList();
        if (getTransaction().isActive()) {
            getTransaction().commit();
        }
        return result;
    }

    @Override
    public void nativeQuery(String query) throws SimpleException {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.createNativeQuery(query).executeUpdate();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    /**
     * @return the puName
     */
    @Override
    public String getPersistenceUnitName() {
        return puName;
    }
}
