package dreamer.card.game.storage;

import dreamer.card.game.ICardGame;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDataBaseManager.class)
public class DataBaseManager implements IDataBaseManager {

    private EntityManagerFactory emf;
    private EntityManager em;

    public DataBaseManager() {
        emf = Persistence.createEntityManagerFactory("Card_Game_InterfacePU");
        em = emf.createEntityManager();
        //initialize the games in the database
        for (ICardGame game : Lookup.getDefault().lookupAll(ICardGame.class)) {
            //Init should generate/update the database entries related to this game, not the pages itself.
            game.init();
        }
    }

    /**
     * @return the emf
     */
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * @return the em
     */
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Object> namedQuery(String query) throws Exception {
        return protectedNamedQuery(query, null, false);
    }

    public List<Object> namedQuery(String query, HashMap<String, Object> parameters) throws Exception {
        return protectedNamedQuery(query, parameters, false);
    }

    @SuppressWarnings("unchecked")
    protected List<Object> protectedNamedQuery(String query, HashMap<String, Object> parameters, boolean locked) throws Exception {
        Query q;
        getTransaction().begin();
        q = getEntityManager().createNamedQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey().toString(), e.getValue());
            }
        }
        List result = q.getResultList();
        if (getTransaction().isActive()) {
            getTransaction().commit();
        }
        return result;
    }

    public void nativeQuery(String query) throws Exception {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.createNativeQuery(query).executeUpdate();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    public void close() {
        getEntityManagerFactory().close();
    }

    private EntityTransaction getTransaction() throws Exception {
        return getEntityManager().getTransaction();
    }
}
