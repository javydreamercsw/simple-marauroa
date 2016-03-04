package com.reflexit.magiccards.core.storage;

import com.reflexit.magiccards.core.model.*;
import com.reflexit.magiccards.core.model.storage.AbstractStorage;
import com.reflexit.magiccards.core.model.storage.db.DBException;
import com.reflexit.magiccards.core.model.storage.db.DataBaseStateListener;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
import com.reflexit.magiccards.core.storage.database.Card;
import com.reflexit.magiccards.core.storage.database.CardAttribute;
import com.reflexit.magiccards.core.storage.database.CardCollection;
import com.reflexit.magiccards.core.storage.database.CardCollectionHasCard;
import com.reflexit.magiccards.core.storage.database.CardCollectionHasCardPK;
import com.reflexit.magiccards.core.storage.database.CardCollectionType;
import com.reflexit.magiccards.core.storage.database.CardHasCardAttribute;
import com.reflexit.magiccards.core.storage.database.CardHasCardAttributePK;
import com.reflexit.magiccards.core.storage.database.CardSet;
import com.reflexit.magiccards.core.storage.database.CardType;
import com.reflexit.magiccards.core.storage.database.Game;
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
public class DataBaseCardStorage<T> extends AbstractStorage<T>
        implements IDataBaseCardStorage<T> {

    private static final Logger LOG
            = Logger.getLogger(DataBaseCardStorage.class.getName());
    private EntityManagerFactory emf;
    private String pu = "Card_Game_InterfacePU";
    private Map<String, String> dataBaseProperties
            = new HashMap<String, String>();
    protected final List<T> list
            = Collections.synchronizedList(new ArrayList<T>());
    private boolean initialized = false;
    protected final ArrayList<DataBaseStateListener> listeners
            = new ArrayList<DataBaseStateListener>();

    @Override
    public void initialize() {
        //Register all using the Lookup
        for (DataBaseStateListener listener
                : Lookup.getDefault().lookupAll(DataBaseStateListener.class)) {
            listeners.add(listener);
        }
        if (!initialized) {
            if (emf == null) {
                if (dataBaseProperties == null
                        || dataBaseProperties.isEmpty()) {
                    emf = Persistence.createEntityManagerFactory(getPU());
                } else {
                    LOG.fine("Provided the following configuration options:");
                    if (LOG.isLoggable(Level.FINE)) {
                        for (Entry<String, String> entry
                                : dataBaseProperties.entrySet()) {
                            LOG.log(Level.FINE, "{0}: {1}",
                                    new Object[]{entry.getKey(),
                                        entry.getValue()});
                        }
                    }
                    emf = Persistence.createEntityManagerFactory(getPU(),
                            dataBaseProperties);
                }
                //This triggers the databaese creation
                EntityManager temp = emf.createEntityManager();
                temp.close();
            }
            for (ICardGame game
                    : Lookup.getDefault().lookupAll(ICardGame.class)) {
                //Init should generate/update the database entries related 
                //to this game, not the pages itself.
                game.init();
            }
            initialized = true;
            ArrayList<DataBaseStateListener> clone
                    = (ArrayList<DataBaseStateListener>) listeners.clone();
            //Notify listeners
            for (DataBaseStateListener listener : clone) {
                listener.initialized();
            }
        }
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> createdQuery(String query,
            HashMap<String, Object> parameters) throws DBException {
        if (!initialized) {
            throw new DBException("Database not initialized yet!");
        }
        Query q;
        EntityManager localEM = getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = localEM.getTransaction();
        transaction.begin();
        q = getEntityManagerFactory().createEntityManager().createQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries
                    = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        List result = q.getResultList();
        transaction.commit();
        localEM.close();
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
    public List<Object> namedQuery(String query,
            HashMap<String, Object> parameters) throws DBException {
        return protectedNamedQuery(query, parameters, false);
    }

    @SuppressWarnings("unchecked")
    protected List<Object> protectedNamedQuery(String query,
            HashMap<String, Object> parameters, boolean locked)
            throws DBException {
        if (!initialized) {
            throw new DBException("Database not initialized yet!");
        }
        Query q;
        EntityManager localEM = getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = localEM.getTransaction();
        transaction.begin();
        q = localEM.createNamedQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries
                    = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        List result = q.getResultList();
        transaction.commit();
        localEM.close();
        return result;
    }

    @Override
    public void nativeQuery(String query) throws DBException {
        if (!initialized) {
            throw new DBException("Database not initialized yet!");
        }
        EntityManager localEM = getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = localEM.getTransaction();
        transaction.begin();
        localEM.createNativeQuery(query).executeUpdate();
        transaction.commit();
        localEM.close();
    }

    @Override
    public void close() {
        if (getEntityManagerFactory() != null) {
            getEntityManagerFactory().close();
        }
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
        CardAttributeJpaController caController
                = new CardAttributeJpaController(getEntityManagerFactory());
        if (!attributeExists(type)) {
            try {
                CardAttribute attr = new CardAttribute();
                attr.setName(type);
                caController.create(attr);
                LOG.log(Level.FINE,
                        "Created attribute: {0} on the database!", type);
            } catch (Exception ex) {
                throw new DBException(ex.toString());
            }
        }
    }

    @Override
    public ICardType createCardType(String type) throws DBException {
        CardTypeJpaController cardTypeController
                = new CardTypeJpaController(getEntityManagerFactory());
        CardType card_type = new CardType(type);
        cardTypeController.create(card_type);
        LOG.log(Level.FINE,
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
            LOG.log(Level.SEVERE, null, ex);
            return true;
        }
    }

    @Override
    public boolean cardExists(String name, ICardSet set) {
        boolean result = false;
        for (ICard card : getCardsForSet(set)) {
            if (card.getName().equals(name)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void createAttributeIfNeeded(String attr)
            throws DBException {
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
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doSave() throws FileNotFoundException {
        //Do nothing
    }

    @Override
    protected boolean doAddCard(T card, ICardSet set) {
        if (card instanceof Card) {
            try {
                Card T = (Card) card;
                HashMap parameters = new HashMap();
                parameters.put("name", T.getName());
                if (namedQuery("Card.findByName", parameters).isEmpty()) {
                    createCard(T.getCardType(), T.getName(), T.getText(), set);
                }
                return true;
            } catch (DBException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    protected boolean doRemoveCard(T card, ICardSet set) {
        if (card instanceof ICard) {
            try {
                //TODO: need to check against set as well
                ICard icard = (ICard) card;
                HashMap parameters = new HashMap();
                parameters.put("name", icard.getName());
                List result = namedQuery("Card.findByName", parameters);
                if (!result.isEmpty()) {
                    new CardJpaController(getEntityManagerFactory()).destroy(
                            ((Card) result.get(0)).getCardPK());
                    return true;
                }
            } catch (IllegalOrphanException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return false;
            } catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return false;
            } catch (DBException ex) {
                LOG.log(Level.SEVERE, null, ex);
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
            LOG.log(Level.SEVERE, null, ex);
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
    public boolean cardSetExists(String name, ICardGame game) {
        boolean result = false;
        for (ICardSet set : game.getGameCardSets()) {
            if (set.getName().equals(name)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean cardTypeExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("CardType.findByName", parameters);
            return result != null && !result.isEmpty();
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ICard updateCard(ICardType type, String name, byte[] text, ICardSet set)
            throws DBException {
        try {
            if (!cardTypeExists(type.getName())) {
                type = (CardType) createCardType(type.getName());
            }
            CardJpaController cardController
                    = new CardJpaController(getEntityManagerFactory());
            //Make sure to get the database object
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List<Object> cards = namedQuery("Card.findByName", parameters);
            if (!cards.isEmpty()) {
                Card card = (Card) cards.get(0);
                card.setCardType((CardType) type);
                card.setText(text);
                card.setSetName(set.getName());
                if (card.getCardSetList() == null) {
                    card.setCardSetList(new ArrayList<CardSet>());
                }
                cardController.edit(card);
                LOG.log(Level.FINE,
                        "Updated card: {0} on the database!", name);
                return (ICard) card;
            }
            return null;
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public CardSet getCardSet(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("CardSet.findByName", parameters);
            if (!result.isEmpty()) {
                return (CardSet) result.get(0);
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ICard createCard(ICardType type, String name, byte[] text, ICardSet set)
            throws DBException {
        try {
            if (!cardTypeExists(type.getName())) {
                type = (CardType) createCardType(type.getName());
            }
            CardJpaController cardController
                    = new CardJpaController(getEntityManagerFactory());
            Card card = new Card(((CardType) type).getId(), name, text);
            card.setCardType((CardType) type);
            if (card.getCardSetList() == null) {
                card.setCardSetList(new ArrayList<CardSet>());
            }
            card.getCardSetList().add(getCardSet(set.getName()));
            cardController.create(card);
            LOG.log(Level.FINE,
                    "Created card: {0} on the database!", name);
            return (ICard) card;
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public ICardHasCardAttribute addAttributeToCard(ICard card,
            String attr, String value)
            throws DBException {
        try {
            CardJpaController cardController
                    = new CardJpaController(getEntityManagerFactory());
            createAttributeIfNeeded(attr);
            ICardAttribute cardAttribute = getCardAttribute(attr);
            CardHasCardAttributeJpaController chcaController
                    = new CardHasCardAttributeJpaController(
                            getEntityManagerFactory());
            CardHasCardAttribute chca
                    = chcaController.findCardHasCardAttribute(
                            new CardHasCardAttributePK(((Card) card).getCardPK().getId(),
                                    ((Card) card).getCardPK().getCardTypeId(),
                                    ((CardAttribute) cardAttribute).getId()));
            boolean add = false;
            if (chca == null) {
                chca = new CardHasCardAttribute(
                        ((Card) card).getCardPK().getId(),
                        ((Card) card).getCardPK().getCardTypeId(),
                        ((CardAttribute) cardAttribute).getId());
                add = true;
            }
            chca.setValue(value);
            chca.setCard(((Card) card));
            chca.setCardAttribute(((CardAttribute) cardAttribute));
            if (add) {
                chcaController.create(chca);
                ((Card) card).getCardHasCardAttributeList().add(chca);
                cardController.edit(((Card) card));
            }
            LOG.log(Level.FINE,
                    "Added attribute: {0} to card: {1} on the database!",
                    new Object[]{attr, card.getName()});
            return chca;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new DBException(ex.toString());
        }
    }

    @Override
    public ICardSet createCardSet(IGame game, String name, String abbreviation,
            Date released) throws DBException {
        try {

            CardSetJpaController csController
                    = new CardSetJpaController(getEntityManagerFactory());
            CardSet cs
                    = new CardSet(((Game) game).getId(), abbreviation,
                            name, released);
            cs.setGame(((Game) game));
            cs.setReleased(released == null ? new Date() : released);
            csController.create(cs);
            LOG.log(Level.FINE,
                    "Created card set: {0} with abbreviation: {1} "
                    + "and release date: {2} on the database!",
                    new Object[]{name,
                        abbreviation, released});
            return (ICardSet) cs;
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public void addCardToSet(ICard card, ICardSet cs) throws DBException {
        try {
            CardSetJpaController csController
                    = new CardSetJpaController(getEntityManagerFactory());
            ((CardSet) cs).getCardList().add((Card) card);
            csController.edit(((CardSet) cs));
            LOG.log(Level.FINE,
                    "Added {0} to set: {1}",
                    new Object[]{card.getName(), cs.getName()});
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error trying to add {0} to set: {1}",
                    new Object[]{card.getName(), cs.getName()});
            throw new DBException(ex.toString());
        }
    }

    @Override
    public void addCardsToSet(List<ICard> cards, ICardSet cs)
            throws DBException {
        for (ICard card : cards) {
            addCardToSet(card, cs);
        }
        LOG.log(Level.FINE,
                "Added {0} to set: {1}",
                new Object[]{cards.size(), cs.getName()});
    }

    @Override
    public String printCardsInSet(ICardSet cs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Game: ").append(
                ((CardSet) cs).getGame().getName()).append('\n')
                .append(cs.getName()).append(":").append('\n');
        for (Card card : ((CardSet) cs).getCardList()) {
            sb.append(card.getName()).append('\n');
        }
        sb.append("----------------------------------------------");
        return sb.toString();
    }

    @Override
    public ICardCollectionType createCardCollectionType(String name)
            throws DBException {
        CardCollectionTypeJpaController controller
                = new CardCollectionTypeJpaController(getEntityManagerFactory());
        CardCollectionType cct = null;
        if (cardCollectionTypeExists(name)) {
            for (CardCollectionType type
                    : controller.findCardCollectionTypeEntities()) {
                if (type.getName().equals(name)) {
                    cct = type;
                    break;
                }
            }
        } else {
            cct = new CardCollectionType(name);
            controller.create(cct);
            LOG.log(Level.FINE,
                    "Created card collection type: {0} on the database!", name);
        }
        return cct != null ? (ICardCollectionType) cct : null;
    }

    @Override
    public ICardCollection createCardCollection(ICardCollectionType type,
            String name) throws DBException {
        try {
            CardCollectionJpaController controller
                    = new CardCollectionJpaController(getEntityManagerFactory());
            CardCollection cc
                    = new CardCollection(((CardCollectionType) type).getId(),
                            name);
            if (cardCollectionExists(name)) {
                for (CardCollection collection
                        : controller.findCardCollectionEntities()) {
                    if (collection.getName().equals(name)) {
                        cc = collection;
                        break;
                    }
                }
            } else {
                cc.setCardCollectionType((CardCollectionType) type);
                controller.create(cc);
                LOG.log(Level.FINE,
                        "Created card collection: {0} on the database!", name);
            }
            return (ICardCollection) cc;
        } catch (Exception ex) {
            throw new DBException(ex.toString());
        }
    }

    @Override
    public ICardCollection addCardsToCollection(HashMap<ICard, Integer> cards,
            ICardCollection collection) throws DBException {
        for (Entry<ICard, Integer> entry : cards.entrySet()) {
            if (entry.getValue() < 0) {
                throw new DBException(
                        "Invalid operation! Tried to add a negative value. "
                        + "Use removeCardsFromCollection instead!");
            }
            CardCollectionHasCardJpaController controller
                    = new CardCollectionHasCardJpaController(
                            getEntityManagerFactory());
            CardCollectionHasCard cchc
                    = controller.findCardCollectionHasCard(
                            new CardCollectionHasCardPK(
                                    ((CardCollection) collection).getCardCollectionPK().getId(),
                                    ((CardCollection) collection).getCardCollectionPK()
                                    .getCardCollectionTypeId(),
                                    ((Card) entry.getKey()).getCardPK().getId(),
                                    ((Card) entry.getKey()).getCardPK().getCardTypeId()));
            if (cchc == null) {
                try {
                    cchc = new CardCollectionHasCard(
                            ((CardCollection) collection).getCardCollectionPK()
                            .getId(),
                            ((CardCollection) collection).getCardCollectionPK()
                            .getCardCollectionTypeId(),
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
                } catch (Exception ex) {
                    throw new DBException(ex.toString());
                }
            }
            LOG.log(Level.FINE,
                    "Added {0} instances of {1} to collection: {2}",
                    new Object[]{entry.getValue(), entry.getKey().getName(),
                        ((CardCollection) collection).getName()});
        }
        CardCollectionJpaController ccController
                = new CardCollectionJpaController(getEntityManagerFactory());
        collection = (ICardCollection) ccController.findCardCollection(
                ((CardCollection) collection).getCardCollectionPK());
        return collection;
    }

    @Override
    public ICardCollection removeCardsFromCollection(
            HashMap<ICard, Integer> cards, ICardCollection collection)
            throws DBException {
        for (Entry<ICard, Integer> entry : cards.entrySet()) {
            if (entry.getValue() < 0) {
                throw new DBException("Invalid operation! Tried to remove a "
                        + "negative value. Use addCardsToCollection instead!");
            }
            CardCollectionHasCardJpaController controller
                    = new CardCollectionHasCardJpaController(
                            getEntityManagerFactory());
            CardCollectionHasCard cchc = controller.findCardCollectionHasCard(
                    new CardCollectionHasCardPK(((CardCollection) collection)
                            .getCardCollectionPK().getId(),
                            ((CardCollection) collection).getCardCollectionPK()
                            .getCardCollectionTypeId(),
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
                    } catch (Exception ex) {
                        throw new DBException(ex.toString());
                    }
                }
                LOG.log(Level.FINE,
                        "Removed {0} instances of {1} from collection: {2}",
                        new Object[]{initialAmount
                            - finalAmount, entry.getKey().getName(),
                            ((CardCollection) collection).getName()});
            }
        }
        CardCollectionJpaController ccController
                = new CardCollectionJpaController(getEntityManagerFactory());
        collection = (ICardCollection) ccController.findCardCollection(
                ((CardCollection) collection).getCardCollectionPK());
        return collection;
    }

    @Override
    public String printCardsCollection(ICardCollection cc) {
        StringBuilder sb = new StringBuilder();
        sb.append(((CardCollection) cc).getCardCollectionType().getName())
                .append(":").append(((CardCollection) cc).getName())
                .append('\n').append("contents:").append('\n');
        for (CardCollectionHasCard card : ((CardCollection) cc)
                .getCardCollectionHasCardList()) {
            sb.append(card.getCard().getName()).append(" X ")
                    .append(card.getAmount()).append('\n');
        }
        sb.append("-----------------------------------------");
        return sb.toString();
    }

    @Override
    public ICardAttribute getCardAttribute(String attr) throws DBException {
        HashMap parameters = new HashMap();
        parameters.put("name", attr);
        List<Object> result = namedQuery("CardAttribute.findByName",
                parameters);
        return (ICardAttribute) (result.isEmpty() ? null : result.get(0));
    }

    @Override
    public void addAttributesToCard(ICard card, Map<String, String> attributes)
            throws DBException {
        for (Entry<String, String> entry : attributes.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null
                    && !entry.getValue().trim().isEmpty()) {
                try {
                    LOG.log(Level.FINE,
                            "Adding attribute: {0} to card: {1} with value: {2}",
                            new Object[]{entry.getKey(), card.getName(),
                                entry.getValue()});
                    CardHasCardAttribute chca
                            = (CardHasCardAttribute) addAttributeToCard(card,
                                    entry.getKey(), entry.getValue());
                    chca.setValue(entry.getValue());
                    CardHasCardAttributeJpaController chcaController
                            = new CardHasCardAttributeJpaController(
                                    getEntityManagerFactory());
                    chcaController.edit(chca);
                    LOG.log(Level.FINE, "Added attribute: {0} to card: {1} "
                            + "with value: {2}",
                            new Object[]{entry.getKey(), card.getName(),
                                entry.getValue()});
                } catch (NonexistentEntityException ex) {
                    throw new DBException(ex.toString());
                } catch (Exception ex) {
                    //Do nothing, no need to add it
                }
            }
        }
    }

    @Override
    public Map<String, String> getAttributesForCard(ICard card) {
        HashMap<String, String> attributes = new HashMap<String, String>();
        for (CardHasCardAttribute attr : ((Card) card).getCardHasCardAttributeList()) {
            attributes.put(attr.getCardAttribute().getName(), attr.getValue());
        }
        return attributes;
    }

    @Override
    public String getCardAttribute(ICard card, String name) {
        try {
            String result = null;
            //Make sure to get the database object
            HashMap parameters = new HashMap();
            parameters.put("name", card.getName());
            List<Object> cards = namedQuery("Card.findByName", parameters);
            if (!cards.isEmpty()) {
                Card dbCard = (Card) cards.get(0);
                for (CardHasCardAttribute chca : dbCard.getCardHasCardAttributeList()) {
                    if (chca.getCardAttribute().getName().equals(name)) {
                        result = chca.getValue();
                        break;
                    }
                }
            }
            return result;
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public IGame createGame(String name) {
        Game game = null;
        GameJpaController controller
                = new GameJpaController(getEntityManagerFactory());
        for (Game g : controller.findGameEntities()) {
            if (g.getName().equals(name)) {
                game = g;
                break;
            }
        }
        if (game == null) {
            game = new Game(name);
            controller.create(game);
        }
        return (IGame) game;
    }

    @Override
    public Map<String, String> getAttributesForCard(String name)
            throws DBException {
        HashMap parameters = new HashMap();
        parameters.put("name", name);
        return getAttributesForCard((ICard) namedQuery("Card.findByName",
                parameters).get(0));
    }

    @Override
    public List<ICard> getCardsForSet(ICardSet set) {
        ArrayList<ICard> cards = new ArrayList<ICard>();
        try {
            //Fill lookup with pages for the selected game
            HashMap parameters = new HashMap();
            parameters.put("name", set.getName());
            List result = Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                    .namedQuery("CardSet.findByName", parameters);
            if (!result.isEmpty()) {
                CardSet temp = (CardSet) result.get(0);
                for (Card card : temp.getCardList()) {
                    card.setSetName(temp.getName());
                    cards.add(card);
                }
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return cards;
    }

    @Override
    public List<IGame> getGames() {
        ArrayList<IGame> games = new ArrayList<IGame>();
        try {
            //Fill lookup with games
            List result = Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                    .namedQuery("Game.findAll");
            if (!result.isEmpty()) {
                Game game = (Game) result.get(0);
                games.add(game);
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return games;
    }

    @Override
    public List<ICardSet> getSetsForGame(IGame game) {
        ArrayList<ICardSet> sets = new ArrayList<ICardSet>();
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", game.getName());
            List<Game> result
                    = Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                    .namedQuery("Game.findByName", parameters);
            if (!result.isEmpty()) {
                Game temp = (Game) result.get(0);
                sets.addAll(temp.getCardSetList());
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return sets;
    }

    @Override
    public List<ICard> getCardsForGame(IGame game) {
        ArrayList<ICard> cards = new ArrayList<ICard>();
        for (ICardSet set : getSetsForGame(game)) {
            for (ICard card : ((CardSet) set).getCardList()) {
                card.setSetName(set.getName());
                cards.add(card);
            }
        }
        return cards;
    }

    public boolean cardCollectionTypeExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("CardCollectionType.findByName",
                    parameters);
            return result != null && !result.isEmpty();
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean cardCollectionExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("CardCollection.findByName", parameters);
            return result != null && !result.isEmpty();
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean setHasCard(ICardSet set, ICard card) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", set.getName());
            List<Object> result = namedQuery("CardSet.findByName", parameters);
            if (result.isEmpty()) {
                return false;
            } else {
                CardSet cs = (CardSet) result.get(0);
                for (Card c : cs.getCardList()) {
                    if (c.getName().equals(card.getName())) {
                        return true;
                    }
                }
                return false;
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
            //Just in case
            return true;
        }
    }

    @Override
    public boolean gameExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            return !namedQuery("Game.findByName", parameters).isEmpty();
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return true;
        }
    }

    @Override
    public void addDataBaseStateListener(DataBaseStateListener dl) {
        synchronized (listeners) {
            if (!listeners.contains(dl)) {
                listeners.add(dl);
            }
        }
    }

    @Override
    public void removeDataBaseStateListener(DataBaseStateListener dl) {
        synchronized (listeners) {
            if (listeners.contains(dl)) {
                listeners.remove(dl);
            }
        }
    }

    @Override
    public Map<String, String> getConnectionSettings() {
        return dataBaseProperties;
    }

    public boolean contains(T card) {
        boolean result = false;
        for (IGame g : getGames()) {
            for (ICardSet s : getSetsForGame(g)) {
                if (contains(card, s)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public ICard getCard(String name, ICardSet set) {
        ICard card = null;
        if (cardExists(name, set)) {
            for (ICard c : getCardsForSet(set)) {
                if (c.getName().equals(name)) {
                    card = c;
                    break;
                }
            }
        }
        return card;
    }

    public ICardType getCardType(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            List result = namedQuery("CardType.findByName", parameters);
            if (!result.isEmpty()) {
                return (CardType) result.get(0);
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public IGame getGame(String name) {
        for (IGame g : getGames()) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }
}
