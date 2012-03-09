package com.reflexit.magiccards.core.storage.database;

import com.reflexit.magiccards.core.model.*;
import com.reflexit.magiccards.core.model.storage.AbstractStorage;
import com.reflexit.magiccards.core.model.storage.db.DBException;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
import com.reflexit.magiccards.core.storage.database.controller.*;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.IllegalOrphanException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.NonexistentEntityException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.PreexistingEntityException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @param <T>
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDataBaseCardStorage.class)
public class DataBaseCardStorage<T> extends AbstractStorage<T> implements IDataBaseCardStorage<T> {

    private static final Logger LOG = Logger.getLogger(DataBaseCardStorage.class.getName());
    private EntityManagerFactory emf;
    private EntityManager em;
    private String pu = "Card_Game_InterfacePU";
    private Map<String, String> dataBaseProperties = null;
    protected final List<T> list = Collections.synchronizedList(new ArrayList<T>());

    public void init() {
        if (emf == null) {
            if (dataBaseProperties == null) {
                emf = Persistence.createEntityManagerFactory(getPU());
            } else {
                LOG.info("Provided the following configuration options:");
                for (Entry<String, String> entry : dataBaseProperties.entrySet()) {
                    LOG.log(Level.INFO, "{0}: {1}", new Object[]{entry.getKey(), entry.getValue()});
                }
                emf = Persistence.createEntityManagerFactory(getPU(), dataBaseProperties);
            }
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> createdQuery(String query, HashMap<String, Object> parameters) throws DBException {
        Query q;
        EntityTransaction transaction = getTransaction();
        transaction.begin();
        q = getEntityManager().createQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey().toString(), e.getValue());
            }
        }
        List result = q.getResultList();
        transaction.commit();
        return result;
    }

    @Override
    public List<Object> createdQuery(String query) throws DBException {
        return createdQuery(query, null);
    }

    @Override
    public List<Object> namedQuery(String query) throws DBException {
        return protectedNamedQuery(query, null, false);
    }

    @Override
    public List<Object> namedQuery(String query, HashMap<String, Object> parameters) throws DBException {
        return protectedNamedQuery(query, parameters, false);
    }

    @SuppressWarnings("unchecked")
    protected List<Object> protectedNamedQuery(String query, HashMap<String, Object> parameters, boolean locked) throws DBException {
        Query q;
        EntityTransaction transaction = getTransaction();
        transaction.begin();
        q = getEntityManager().createNamedQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey().toString(), e.getValue());
            }
        }
        List result = q.getResultList();
        transaction.commit();
        return result;
    }

    @Override
    public void nativeQuery(String query) throws DBException {
        EntityManager localEM = getEntityManager();
        EntityTransaction transaction = localEM.getTransaction();
        transaction.begin();
        localEM.createNativeQuery(query).executeUpdate();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    @Override
    public void close() {
        getEntityManagerFactory().close();
    }

    private EntityTransaction getTransaction() throws DBException {
        return getEntityManagerFactory().createEntityManager().getTransaction();
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
    @Override
    public void setPU(String pu) {
        this.pu = pu;
    }

    @Override
    public void createAttributes(String type) throws DBException {
        //Add Attributes
        CardAttributeJpaController caController = new CardAttributeJpaController(getEntityManagerFactory());
        if (!attributeExists(type)) {
            try {
                CardAttribute attr = new CardAttribute();
                attr.setName(type);
                caController.create(attr);
                LOG.log(Level.FINE,
                        "Created attribute: {0} on the database!", type);
            } catch (PreexistingEntityException ex) {
                throw new DBException(ex.toString());
            } catch (Exception ex) {
                throw new DBException(ex.toString());
            }
        }
    }

    @Override
    public ICardType createCardType(String type) throws DBException {
        CardTypeJpaController cardTypeController = new CardTypeJpaController(getEntityManagerFactory());
        CardType card_type = new CardType(type);
        cardTypeController.create(card_type);
        Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE,
                "Created card type: {0} on the database!", type);
        return card_type;
    }

    @Override
    public boolean attributeExists(String attr) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", attr);
            List result = namedQuery("CardAttribute.findByName", parameters);
            return result != null && !result.isEmpty();
        } catch (DBException ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    @Override
    public boolean cardExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("Card.findByName", parameters);
            return result != null && !result.isEmpty();
        } catch (DBException ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    @Override
    public void createAttributeIfNeeded(String attr, String value) throws DBException {
        if (!attributeExists(attr)) {
            createAttributes(attr);
            LOG.log(Level.FINE, "Created attribute: {0}", new Object[]{attr});
        }
    }

    @Override
    public void setDataBaseProperties(Map<String, String> dataBaseProperties) {
        this.dataBaseProperties = dataBaseProperties;
    }

    @Override
    public void clearCache() {
        //Do nothing
    }

    @Override
    protected void doLoad() {
        try {
            List result = namedQuery("Card.findAll");
            synchronized (list) {
                list.clear();
                for (Iterator it = result.iterator(); it.hasNext();) {
                    list.add((T) it.next());
                }
            }
        } catch (DBException ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doSave() throws FileNotFoundException {
        //Do nothing
    }

    @Override
    protected boolean doAddCard(T card) {
        if (card instanceof Card) {
            try {
                Card T = (Card) card;
                HashMap parameters = new HashMap();
                parameters.put("name", T.getName());
                if (namedQuery("Card.findByName", parameters).isEmpty()) {
                    createCard(T.getCardType(), T.getName(), T.getText());
                }
                return true;
            } catch (DBException ex) {
                Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    protected boolean doRemoveCard(T card) {
        if (card instanceof ICard) {
            try {
                ICard icard = (ICard) card;
                HashMap parameters = new HashMap();
                parameters.put("name", icard.getName());
                List result = namedQuery("Card.findByName", parameters);
                if (!result.isEmpty()) {
                    new CardJpaController(getEntityManagerFactory()).destroy(((Card) result.get(0)).getCardPK());
                    return true;
                }
            } catch (IllegalOrphanException ex) {
                Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (DBException ex) {
                Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "db";
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public int size() {
        try {
            return namedQuery("Card.findAll").size();
        } catch (DBException ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public Iterator<T> iterator() {
        synchronized (list) {
            ArrayList x = new ArrayList(list);
            return x.iterator();
        }
    }

    @Override
    public boolean cardSetExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("CardSet.findByName", parameters);
            return result != null && !result.isEmpty();
        } catch (DBException ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean cardTypeExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("CardType.findByName", parameters);
            return result != null && !result.isEmpty();
        } catch (DBException ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public ICard createCard(ICardType type, String name, byte[] text) throws DBException {
        try {
            if (!cardTypeExists(type.getName())) {
                type = (CardType) createCardType(type.getName());
            }
            CardJpaController cardController = new CardJpaController(getEntityManagerFactory());
            Card card = new Card(((CardType) type).getId(), name, text);
            card.setCardType((CardType) type);
            cardController.create(card);
            Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE,
                    "Created card: {0} on the database!", name);
            return (ICard) card;
        } catch (PreexistingEntityException ex) {
            throw new DBException(ex.toString());
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public ICardHasCardAttribute addAttributeToCard(ICard card, ICardAttribute attr) throws DBException {
        try {
            CardJpaController cardController = new CardJpaController(getEntityManagerFactory());
            CardHasCardAttributeJpaController chcaController = new CardHasCardAttributeJpaController(getEntityManagerFactory());
            CardHasCardAttribute chca = new CardHasCardAttribute(((Card) card).getCardPK().getId(),
                    ((Card) card).getCardPK().getCardTypeId(), ((CardAttribute) attr).getId());
            chca.setValue("test");
            chca.setCard(((Card) card));
            chca.setCardAttribute(((CardAttribute) attr));
            chcaController.create(chca);
            ((Card) card).getCardHasCardAttributeList().add(chca);
            cardController.edit(((Card) card));
            Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE, "Added attribute: {0} to card: {1} on the database!", new Object[]{attr.getName(), card.getName()});
            return chca;
        } catch (PreexistingEntityException ex) {
            throw new DBException(ex.toString());
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public ICardSet createCardSet(IGame game, String name, String abbreviation, Date released) throws DBException {
        try {

            CardSetJpaController csController = new CardSetJpaController(getEntityManagerFactory());
            CardSet cs = new CardSet(((Game) game).getId(), abbreviation, name, released);
            cs.setGame(((Game) game));
            cs.setReleased(released == null ? new Date() : released);
            csController.create(cs);
            Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE,
                    "Created card set: {0} with abbreviation: {1} "
                    + "and release date: {2} on the database!", new Object[]{name, abbreviation, released});
            return (ICardSet) cs;
        } catch (PreexistingEntityException ex) {
            throw new DBException(ex.toString());
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public void addCardToSet(ICard card, ICardSet cs) throws DBException {
        try {
            CardSetJpaController csController = new CardSetJpaController(getEntityManagerFactory());
            ((CardSet) cs).getCardList().add((Card) card);
            csController.edit(((CardSet) cs));
            Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE, "Added {0} to set: {1}", new Object[]{card.getName(), cs.getName()});
        } catch (NonexistentEntityException ex) {
            throw new DBException(ex.toString());
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public void addCardsToSet(List<ICard> cards, ICardSet cs) throws DBException {
        for (ICard card : cards) {
            addCardToSet(card, cs);
        }
        Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE, "Added {0} to set: {1}", new Object[]{cards.size(), cs.getName()});
    }

    @Override
    public String printCardsInSet(ICardSet cs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Game: ").append(((CardSet) cs).getGame().getName()).append('\n').append(cs.getName()).append(":").append('\n');
        for (Iterator<Card> it = ((CardSet) cs).getCardList().iterator(); it.hasNext();) {
            Card card = it.next();
            sb.append(card.getName()).append('\n');
        }
        sb.append("----------------------------------------------");
        return sb.toString();
    }

    @Override
    public ICardCollectionType createCardCollectionType(String name) throws DBException {
        CardCollectionTypeJpaController controller = new CardCollectionTypeJpaController(getEntityManagerFactory());
        CardCollectionType cct = new CardCollectionType(name);
        controller.create(cct);
        Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE, "Created card collection type: {0} on the database!", name);
        return (ICardCollectionType) cct;
    }

    @Override
    public ICardCollection createCardCollection(ICardCollectionType type, String name) throws DBException {
        try {
            CardCollectionJpaController controller = new CardCollectionJpaController(getEntityManagerFactory());
            CardCollection cc = new CardCollection(((CardCollectionType) type).getId(), name);
            cc.setCardCollectionType((CardCollectionType) type);
            controller.create(cc);
            Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE, "Created card collection: {0} on the database!", name);
            return (ICardCollection) cc;
        } catch (PreexistingEntityException ex) {
            throw new DBException(ex.toString());
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public ICardCollection addCardsToCollection(HashMap<ICard, Integer> cards, ICardCollection collection) throws DBException {
        for (Iterator<Entry<ICard, Integer>> it = cards.entrySet().iterator(); it.hasNext();) {
            Entry<ICard, Integer> entry = it.next();
            if (entry.getValue() < 0) {
                throw new DBException("Invalid operation! Tried to add a negative value. Use removeCardsFromCollection instead!");
            }
            CardCollectionHasCardJpaController controller = new CardCollectionHasCardJpaController(getEntityManagerFactory());
            CardCollectionHasCard cchc = controller.findCardCollectionHasCard(new CardCollectionHasCardPK(
                    ((CardCollection) collection).getCardCollectionPK().getId(),
                    ((CardCollection) collection).getCardCollectionPK().getCardCollectionTypeId(),
                    ((Card) entry.getKey()).getCardPK().getId(), ((Card) entry.getKey()).getCardPK().getCardTypeId()));
            if (cchc == null) {
                try {
                    cchc = new CardCollectionHasCard(
                            ((CardCollection) collection).getCardCollectionPK().getId(),
                            ((CardCollection) collection).getCardCollectionPK().getCardCollectionTypeId(),
                            ((Card) entry.getKey()).getCardPK().getId(),
                            ((Card) entry.getKey()).getCardPK().getCardTypeId(),
                            entry.getValue());
                    cchc.setCard(((Card) entry.getKey()));
                    cchc.setCardCollection(((CardCollection) collection));
                    controller.create(cchc);
                } catch (PreexistingEntityException ex) {
                    throw new DBException(ex.toString());
                } catch (Exception ex) {
                    throw new DBException(ex.toString());
                }
            } else {
                cchc.setAmount(cchc.getAmount() + entry.getValue());
                try {
                    controller.edit(cchc);
                } catch (NonexistentEntityException ex) {
                    throw new DBException(ex.toString());
                } catch (Exception ex) {
                    throw new DBException(ex.toString());
                }
            }
            Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE,
                    "Added {0} instances of {1} to collection: {2}",
                    new Object[]{entry.getValue(), entry.getKey().getName(), ((CardCollection) collection).getName()});
        }
        CardCollectionJpaController ccController = new CardCollectionJpaController(getEntityManagerFactory());
        collection = (ICardCollection) ccController.findCardCollection(((CardCollection) collection).getCardCollectionPK());
        return collection;
    }

    @Override
    public ICardCollection removeCardsFromCollection(HashMap<ICard, Integer> cards, ICardCollection collection) throws DBException {
        for (Entry<ICard, Integer> entry : cards.entrySet()) {
            if (entry.getValue() < 0) {
                throw new DBException("Invalid operation! Tried to remove a negative value. Use addCardsToCollection instead!");
            }
            CardCollectionHasCardJpaController controller = new CardCollectionHasCardJpaController(getEntityManagerFactory());
            CardCollectionHasCard cchc = controller.findCardCollectionHasCard(
                    new CardCollectionHasCardPK(((CardCollection) collection).getCardCollectionPK().getId(),
                    ((CardCollection) collection).getCardCollectionPK().getCardCollectionTypeId(),
                    ((Card) entry.getKey()).getCardPK().getId(),
                    ((Card) entry.getKey()).getCardPK().getCardTypeId()));
            if (cchc != null) {
                int initialAmount = cchc.getAmount();
                int finalAmount = initialAmount - entry.getValue();
                if (finalAmount < 0) {
                    finalAmount = 0;
                }
                if (finalAmount == 0) {
                    try {
                        //Remove it
                        controller.destroy(cchc.getCardCollectionHasCardPK());
                    } catch (NonexistentEntityException ex) {
                        throw new DBException(ex.toString());
                    }
                } else {
                    try {
                        cchc.setAmount(finalAmount);
                        controller.edit(cchc);
                    } catch (NonexistentEntityException ex) {
                        throw new DBException(ex.toString());
                    } catch (Exception ex) {
                        throw new DBException(ex.toString());
                    }
                }
                Logger.getLogger(IDataBaseCardStorage.class.getName()).log(Level.FINE,
                        "Removed {0} instances of {1} from collection: {2}",
                        new Object[]{initialAmount - finalAmount, entry.getKey().getName(), ((CardCollection) collection).getName()});
            }
        }
        CardCollectionJpaController ccController = new CardCollectionJpaController(getEntityManagerFactory());
        collection = (ICardCollection) ccController.findCardCollection(((CardCollection) collection).getCardCollectionPK());
        return collection;
    }

    @Override
    public String printCardsCollection(ICardCollection cc) {
        StringBuilder sb = new StringBuilder();
        sb.append(((CardCollection) cc).getCardCollectionType().getName()).append(":").append(((CardCollection) cc).getName()).append('\n').append("contents:").append('\n');
        for (Iterator<CardCollectionHasCard> it = ((CardCollection) cc).getCardCollectionHasCardList().iterator(); it.hasNext();) {
            CardCollectionHasCard card = it.next();
            sb.append(card.getCard().getName()).append(" X ").append(card.getAmount()).append('\n');
        }
        sb.append("-----------------------------------------");
        return sb.toString();
    }

    @Override
    public ICardAttribute getCardAttribute(String attr) throws DBException {
        HashMap parameters = new HashMap();
        parameters.put("name", attr);
        return (ICardAttribute) namedQuery("CardAttribute.findByName", parameters).get(0);
    }

    @Override
    public void addAttributesToCard(ICard card, Map<String, String> attributes) throws DBException {
        for (Entry<String, String> entry : attributes.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                try {
                    LOG.log(Level.FINE, "Adding attribute: {0} to card: {1} with value: {2}",
                            new Object[]{entry.getKey(), card.getName(), entry.getValue()});
                    createAttributeIfNeeded(entry.getKey(), entry.getValue());
                    ICardAttribute cardAttribute = getCardAttribute(entry.getKey());
                    CardHasCardAttribute chca = (CardHasCardAttribute) addAttributeToCard(card, cardAttribute);
                    chca.setValue(entry.getValue());
                    CardHasCardAttributeJpaController chcaController = new CardHasCardAttributeJpaController(getEntityManagerFactory());
                    chcaController.edit(chca);
                    LOG.log(Level.FINE, "Added attribute: {0} to card: {1} with value: {2}",
                            new Object[]{entry.getKey(), card.getName(), entry.getValue()});
                } catch (NonexistentEntityException ex) {
                    throw new DBException(ex.toString());
                } catch (PreexistingEntityException ex) {
                    //Do nothing, no need to add it
                } catch (Exception ex) {
                    //Do nothing, no need to add it
                }
            }
        }
    }

    @Override
    public Map<String, String> getAttributesForCard(ICard card) {
        HashMap<String, String> attributes = new HashMap<String, String>();
        for (Iterator<CardHasCardAttribute> it = ((Card) card).getCardHasCardAttributeList().iterator(); it.hasNext();) {
            CardHasCardAttribute attr = it.next();
            attributes.put(attr.getCardAttribute().getName(), attr.getValue());
        }
        return attributes;
    }

    @Override
    public String getCardAttribute(ICard card, String name) {
        String result = null;
        for (Iterator<CardHasCardAttribute> it = ((Card) card).getCardHasCardAttributeList().iterator(); it.hasNext();) {
            CardHasCardAttribute chca = it.next();
            if (chca.getCardAttribute().getName().equals(name)) {
                result = chca.getValue();
                break;
            }
        }
        return result;
    }

    @Override
    public IGame createGame(String name) {
        GameJpaController controller = new GameJpaController(getEntityManagerFactory());
        Game game = new Game(name);
        controller.create(game);
        return (IGame) game;
    }

    @Override
    public Map<String, String> getAttributesForCard(String name) throws DBException {
        HashMap parameters = new HashMap();
        parameters.put("name", name);
        return getAttributesForCard((ICard) namedQuery("Card.findByName", parameters).get(0));
    }

    @Override
    public void initialize() {
        getEntityManagerFactory();
    }
}
