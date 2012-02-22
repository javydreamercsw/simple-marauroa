package dreamer.card.game.storage;

import dreamer.card.game.DefaultCardGame;
import dreamer.card.game.ICardGame;
import dreamer.card.game.storage.database.persistence.*;
import dreamer.card.game.storage.database.persistence.controller.*;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private String pu;

    public void init() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(getPU());
            if (em != null) {
                em.close();
            }
            em = null;
        }
        if (em == null) {
            em = emf.createEntityManager();
        }
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
        if (emf == null) {
            init();
        }
        return emf;
    }

    /**
     * @return the em
     */
    public EntityManager getEntityManager() {
        if (em == null) {
            init();
        }
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

    /**
     * @return the pu
     */
    public String getPU() {
        return pu;
    }

    /**
     * @param pu the pu to set
     */
    public void setPU(String pu) {
        this.pu = pu;
    }

    public void createAttributes(String type, List<String> values) {
        CardAttributeTypeJpaController catController = new CardAttributeTypeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        HashMap parameters = new HashMap();
        String value = type;
        parameters.put("name", value);
        CardAttributeType attrType = null;
        try {
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttributeType.findByName", parameters);
            if (result.isEmpty()) {
                attrType = new CardAttributeType(value);
                catController.create(attrType);
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.ALL,
                        "Created attribute type: " + value + " on the database!");
            } else {
                attrType = (CardAttributeType) result.get(0);
            }
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Add Card Attributes
        for (String attribute : values) {
            CardAttributeJpaController caController = new CardAttributeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
            parameters.clear();
            parameters.put("name", attribute);
            try {
                List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                if (result.isEmpty()) {
                    CardAttribute attr = new CardAttribute(attrType.getId());
                    attr.setName(attribute);
                    attr.setCardAttributeType(attrType);
                    caController.create(attr);
                    Logger.getLogger(DefaultCardGame.class.getName()).log(Level.ALL,
                            "Created attribute: " + attribute + " on the database!");
                }
            } catch (Exception ex) {
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public CardType createCardType(String type) {
        CardTypeJpaController cardTypeController = new CardTypeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        CardType card_type = new CardType(type);
        try {
            cardTypeController.create(card_type);
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return card_type;
    }

    @Override
    public Card createCard(CardType type, String name, byte[] text) {
        CardJpaController cardController = new CardJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        Card card = new Card(type.getId(), name, text);
        card.setCardType(type);
        try {
            cardController.create(card);
        } catch (PreexistingEntityException ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return card;
    }

    @Override
    public void addAttributeToCard(Card card, CardAttribute attr, String value) {
        try {
            CardJpaController cardController = new CardJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
            CardHasCardAttributeJpaController chcaController = new CardHasCardAttributeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
            CardHasCardAttribute chca = new CardHasCardAttribute(card.getCardPK().getId(),
                    card.getCardPK().getCardTypeId(), attr.getCardAttributePK().getId(),
                    attr.getCardAttributePK().getCardAttributeTypeId());
            chca.setValue("test");
            chca.setCard(card);
            chca.setCardAttribute(attr);
            chcaController.create(chca);
            card.getCardHasCardAttributeList().add(chca);
            cardController.edit(card);
        } catch (PreexistingEntityException ex) {
            Logger.getLogger(DataBaseManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DataBaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
