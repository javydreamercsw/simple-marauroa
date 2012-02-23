package dreamer.card.game.storage;

import dreamer.card.game.DefaultCardGame;
import dreamer.card.game.ICardGame;
import dreamer.card.game.storage.database.persistence.*;
import dreamer.card.game.storage.database.persistence.controller.*;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.util.*;
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

    public void createAttributes(String type, List<String> values) throws Exception {
        CardAttributeTypeJpaController catController = new CardAttributeTypeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        HashMap parameters = new HashMap();
        String value = type;
        parameters.put("name", value);
        CardAttributeType attrType = null;
        List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttributeType.findByName", parameters);
        if (result.isEmpty()) {
            attrType = new CardAttributeType(value);
            catController.create(attrType);
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.FINE,
                    "Created attribute type: " + value + " on the database!");
        } else {
            attrType = (CardAttributeType) result.get(0);
        }
        //Add Attributes
        for (String attribute : values) {
            CardAttributeJpaController caController = new CardAttributeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
            parameters.clear();
            parameters.put("name", attribute);
            result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
            if (result.isEmpty()) {
                CardAttribute attr = new CardAttribute(attrType.getId());
                attr.setName(attribute);
                attr.setCardAttributeType(attrType);
                caController.create(attr);
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.FINE,
                        "Created attribute: " + attribute + " on the database!");
            }
        }
    }

    @Override
    public CardType createCardType(String type) throws Exception {
        CardTypeJpaController cardTypeController = new CardTypeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        CardType card_type = new CardType(type);
        cardTypeController.create(card_type);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card type: " + type + " on the database!");
        return card_type;
    }

    @Override
    public Card createCard(CardType type, String name, byte[] text) throws PreexistingEntityException, Exception {
        CardJpaController cardController = new CardJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        Card card = new Card(type.getId(), name, text);
        card.setCardType(type);
        cardController.create(card);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card: " + name + " on the database!");
        return card;
    }

    @Override
    public void addAttributeToCard(Card card, CardAttribute attr, String value) throws PreexistingEntityException, Exception {
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
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Added attribute: " + attr.getName() + " with value: " + value + " on the database!");
    }

    @Override
    public CardSet createCardSet(Game game, String name, String abbreviation, Date released) throws PreexistingEntityException, Exception {
        CardSetJpaController csController = new CardSetJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        CardSet cs = new CardSet(game.getId(), abbreviation, name, released);
        cs.setGame(game);
        csController.create(cs);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card set: " + name + " on the database!");
        return cs;
    }

    @Override
    public void addCardsToSet(List<Card> cards, CardSet cs) throws NonexistentEntityException, Exception {
        CardSetJpaController csController = new CardSetJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        cs.getCardList().addAll(cards);
        csController.edit(cs);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Added " + cards.size() + " to set: " + cs.getName());
    }

    @Override
    public String printCardsInSet(CardSet cs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Game: " + cs.getGame().getName()).append('\n').append(cs.getName() + ":").append('\n');
        for (Card card : cs.getCardList()) {
            sb.append(card.getName()).append('\n');
        }
        sb.append("----------------------------------------------");
        return sb.toString();
    }

    @Override
    public CardCollectionType createCardCollectionType(String name) throws PreexistingEntityException, Exception {
        CardCollectionTypeJpaController controller = new CardCollectionTypeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        CardCollectionType cct = new CardCollectionType(name);
        controller.create(cct);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card collection type: " + name + " on the database!");
        return cct;
    }

    @Override
    public CardCollection createCardCollection(CardCollectionType type, String name) throws PreexistingEntityException, Exception {
        CardCollectionJpaController controller = new CardCollectionJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        CardCollection cc = new CardCollection(type.getId(), name);
        cc.setCardCollectionType(type);
        controller.create(cc);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card collection: " + name + " on the database!");
        return cc;
    }

    @Override
    public CardCollection addCardsToCollection(HashMap<Card, Integer> cards, CardCollection collection) throws PreexistingEntityException, Exception {
        for (Entry<Card, Integer> entry : cards.entrySet()) {
            if (entry.getValue() < 0) {
                throw new Exception("Invalid operation! Tried to add a negative value. Use removeCardsFromCollection instead!");
            }
            CardCollectionHasCardJpaController controller = new CardCollectionHasCardJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
            CardCollectionHasCard cchc = controller.findCardCollectionHasCard(new CardCollectionHasCardPK(collection.getCardCollectionPK().getId(), collection.getCardCollectionPK().getCardCollectionTypeId(),
                    entry.getKey().getCardPK().getId(), entry.getKey().getCardPK().getCardTypeId()));
            if (cchc == null) {
                cchc = new CardCollectionHasCard(collection.getCardCollectionPK().getId(), collection.getCardCollectionPK().getCardCollectionTypeId(),
                        entry.getKey().getCardPK().getId(), entry.getKey().getCardPK().getCardTypeId(), entry.getValue());
                cchc.setCard(entry.getKey());
                cchc.setCardCollection(collection);
                controller.create(cchc);
            } else {
                cchc.setAmount(cchc.getAmount() + entry.getValue());
                controller.edit(cchc);
            }
            Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                    "Added " + entry.getValue() + " instances of " + entry.getKey().getName() + " to collection: " + collection.getName());
        }
        CardCollectionJpaController ccController = new CardCollectionJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        collection = ccController.findCardCollection(collection.getCardCollectionPK());
        return collection;
    }

    @Override
    public CardCollection removeCardsFromCollection(HashMap<Card, Integer> cards, CardCollection collection) throws PreexistingEntityException, Exception {
        for (Entry<Card, Integer> entry : cards.entrySet()) {
            if (entry.getValue() < 0) {
                throw new Exception("Invalid operation! Tried to remove a negative value. Use addCardsToCollection instead!");
            }
            CardCollectionHasCardJpaController controller = new CardCollectionHasCardJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
            CardCollectionHasCard cchc = controller.findCardCollectionHasCard(new CardCollectionHasCardPK(collection.getCardCollectionPK().getId(), collection.getCardCollectionPK().getCardCollectionTypeId(),
                    entry.getKey().getCardPK().getId(), entry.getKey().getCardPK().getCardTypeId()));
            if (cchc != null) {
                int initialAmount = cchc.getAmount();
                int finalAmount = initialAmount - entry.getValue();
                if (finalAmount < 0) {
                    finalAmount = 0;
                }
                if (finalAmount == 0) {
                    //Remove it
                    controller.destroy(cchc.getCardCollectionHasCardPK());
                } else {
                    cchc.setAmount(finalAmount);
                    controller.edit(cchc);
                }
                Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                        "Removed " + (initialAmount - finalAmount) + " instances of " + entry.getKey().getName() + " from collection: " + collection.getName());
            }
        }
        CardCollectionJpaController ccController = new CardCollectionJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        collection = ccController.findCardCollection(collection.getCardCollectionPK());
        return collection;
    }

    @Override
    public String printCardsCollection(CardCollection cc) {
        StringBuilder sb = new StringBuilder();
        sb.append(cc.getCardCollectionType().getName()).append(":").append(cc.getName()).append('\n').append("contents:").append('\n');
        for (CardCollectionHasCard card : cc.getCardCollectionHasCardList()) {
            sb.append(card.getCard().getName()).append(" X ").append(card.getAmount()).append('\n');
        }
        sb.append("-----------------------------------------");
        return sb.toString();
    }
}
