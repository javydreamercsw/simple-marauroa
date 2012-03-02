package dreamer.card.game.storage;

import dreamer.card.game.ICard;
import dreamer.card.game.ICardGame;
import dreamer.card.game.storage.database.persistence.*;
import dreamer.card.game.storage.database.persistence.controller.*;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
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
@ServiceProvider(service = IDataBaseManager.class)
public class DataBaseCardStorage<T> extends AbstractStorage<T> implements IDataBaseManager<T> {

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
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            init();
        }
        return emf;
    }

    /**
     * @return the em
     */
    @Override
    public EntityManager getEntityManager() {
        if (em == null) {
            init();
        }
        return em;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> createdQuery(String query, HashMap<String, Object> parameters) throws Exception {
        Query q;
        getTransaction().begin();
        q = getEntityManager().createQuery(query);
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

    @Override
    public List<Object> createdQuery(String query) throws Exception {
        return createdQuery(query, null);
    }

    @Override
    public List<Object> namedQuery(String query) throws Exception {
        return protectedNamedQuery(query, null, false);
    }

    @Override
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

    @Override
    public void nativeQuery(String query) throws Exception {
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
    @Override
    public void setPU(String pu) {
        this.pu = pu;
    }

    @Override
    public void createAttributes(String type) throws Exception {
        //Add Attributes
        CardAttributeJpaController caController = new CardAttributeJpaController(getEntityManagerFactory());
        if (!attributeExists(type)) {
            CardAttribute attr = new CardAttribute();
            attr.setName(type);
            caController.create(attr);
            LOG.log(Level.FINE,
                    "Created attribute: {0} on the database!", type);
        }
    }

    @Override
    public CardType createCardType(String type) throws Exception {
        CardTypeJpaController cardTypeController = new CardTypeJpaController(getEntityManagerFactory());
        CardType card_type = new CardType(type);
        cardTypeController.create(card_type);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card type: {0} on the database!", type);
        return card_type;
    }

    @Override
    public Card createCard(CardType type, String name, byte[] text) throws PreexistingEntityException, Exception {
        if (!cardTypeExists(type.getName())) {
            type = createCardType(type.getName());
        }
        CardJpaController cardController = new CardJpaController(getEntityManagerFactory());
        Card card = new Card(type.getId(), name, text);
        card.setCardType(type);
        cardController.create(card);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card: {0} on the database!", name);
        return card;
    }

    @Override
    public CardHasCardAttribute addAttributeToCard(Card card, CardAttribute attr) throws PreexistingEntityException, Exception {
        CardJpaController cardController = new CardJpaController(getEntityManagerFactory());
        CardHasCardAttributeJpaController chcaController = new CardHasCardAttributeJpaController(getEntityManagerFactory());
        CardHasCardAttribute chca = new CardHasCardAttribute(card.getCardPK().getId(),
                card.getCardPK().getCardTypeId(), attr.getId());
        chca.setValue("test");
        chca.setCard(card);
        chca.setCardAttribute(attr);
        chcaController.create(chca);
        card.getCardHasCardAttributeList().add(chca);
        cardController.edit(card);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE, "Added attribute: {0} to card: {1} on the database!", new Object[]{attr.getName(), card.getName()});
        return chca;
    }

    @Override
    public CardSet createCardSet(Game game, String name, String abbreviation, Date released) throws PreexistingEntityException, Exception {
        CardSetJpaController csController = new CardSetJpaController(getEntityManagerFactory());
        CardSet cs = new CardSet(game.getId(), abbreviation, name, released);
        cs.setGame(game);
        cs.setReleased(released == null ? new Date() : released);
        csController.create(cs);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE,
                "Created card set: {0} with abbreviation: {1} "
                + "and release date: {2} on the database!", new Object[]{name, abbreviation, released});
        return cs;
    }

    @Override
    public void addCardsToSet(List<Card> cards, CardSet cs) throws NonexistentEntityException, Exception {
        CardSetJpaController csController = new CardSetJpaController(getEntityManagerFactory());
        cs.getCardList().addAll(cards);
        csController.edit(cs);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE, "Added {0} to set: {1}", new Object[]{cards.size(), cs.getName()});
    }

    @Override
    public String printCardsInSet(CardSet cs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Game: ").append(cs.getGame().getName()).append('\n').append(cs.getName()).append(":").append('\n');
        for (Card card : cs.getCardList()) {
            sb.append(card.getName()).append('\n');
        }
        sb.append("----------------------------------------------");
        return sb.toString();
    }

    @Override
    public CardCollectionType createCardCollectionType(String name) throws PreexistingEntityException, Exception {
        CardCollectionTypeJpaController controller = new CardCollectionTypeJpaController(getEntityManagerFactory());
        CardCollectionType cct = new CardCollectionType(name);
        controller.create(cct);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE, "Created card collection type: {0} on the database!", name);
        return cct;
    }

    @Override
    public CardCollection createCardCollection(CardCollectionType type, String name) throws PreexistingEntityException, Exception {
        CardCollectionJpaController controller = new CardCollectionJpaController(getEntityManagerFactory());
        CardCollection cc = new CardCollection(type.getId(), name);
        cc.setCardCollectionType(type);
        controller.create(cc);
        Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE, "Created card collection: {0} on the database!", name);
        return cc;
    }

    @Override
    public CardCollection addCardsToCollection(HashMap<Card, Integer> cards, CardCollection collection) throws PreexistingEntityException, Exception {
        for (Entry<Card, Integer> entry : cards.entrySet()) {
            if (entry.getValue() < 0) {
                throw new Exception("Invalid operation! Tried to add a negative value. Use removeCardsFromCollection instead!");
            }
            CardCollectionHasCardJpaController controller = new CardCollectionHasCardJpaController(getEntityManagerFactory());
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
            Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE, "Added {0} instances of {1} to collection: {2}", new Object[]{entry.getValue(), entry.getKey().getName(), collection.getName()});
        }
        CardCollectionJpaController ccController = new CardCollectionJpaController(getEntityManagerFactory());
        collection = ccController.findCardCollection(collection.getCardCollectionPK());
        return collection;
    }

    @Override
    public CardCollection removeCardsFromCollection(HashMap<Card, Integer> cards, CardCollection collection) throws PreexistingEntityException, Exception {
        for (Entry<Card, Integer> entry : cards.entrySet()) {
            if (entry.getValue() < 0) {
                throw new Exception("Invalid operation! Tried to remove a negative value. Use addCardsToCollection instead!");
            }
            CardCollectionHasCardJpaController controller = new CardCollectionHasCardJpaController(getEntityManagerFactory());
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
                Logger.getLogger(IDataBaseManager.class.getName()).log(Level.FINE, "Removed {0} instances of {1} from collection: {2}", new Object[]{initialAmount - finalAmount, entry.getKey().getName(), collection.getName()});
            }
        }
        CardCollectionJpaController ccController = new CardCollectionJpaController(getEntityManagerFactory());
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

    @Override
    public boolean attributeExists(String attr) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", attr);
            List result = null;
            try {
                result = namedQuery("CardAttribute.findByName", parameters);
            } catch (Exception ex) {
                Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
            return result != null && !result.isEmpty();
        } catch (Exception ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    @Override
    public CardAttribute getCardAttribute(String attr) throws Exception {
        HashMap parameters = new HashMap();
        parameters.put("name", attr);
        return (CardAttribute) namedQuery("CardAttribute.findByName", parameters).get(0);
    }

    @Override
    public void addAttributesToCard(Card card, Map<String, String> attributes) throws Exception {
        for (Entry<String, String> entry : attributes.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                createAttributeIfNeeded(entry.getKey(), entry.getValue());
                CardAttribute cardAttribute = getCardAttribute(entry.getKey());
                CardHasCardAttribute chca = addAttributeToCard(card, cardAttribute);
                chca.setValue(entry.getValue());
                CardHasCardAttributeJpaController chcaController = new CardHasCardAttributeJpaController(getEntityManagerFactory());
                chcaController.edit(chca);
                LOG.log(Level.FINE, "Added attribute: {0} to card: {1} with value: {2}",
                        new Object[]{entry.getKey(), card.getName(), entry.getValue()});
            }
        }
    }

    @Override
    public void createAttributeIfNeeded(String attr, String value) throws Exception {
        if (!attributeExists(attr)) {
            createAttributes(attr);
            LOG.log(Level.FINE, "Created attribute: {0}", new Object[]{attr});
        }
    }

    @Override
    public Map<String, String> getAttributesForCard(String name) throws Exception {
        HashMap parameters = new HashMap();
        parameters.put("name", name);
        return getAttributesForCard((Card) namedQuery("Card.findByName", parameters).get(0));
    }

    @Override
    public Map<String, String> getAttributesForCard(Card card) {
        HashMap<String, String> attributes = new HashMap<String, String>();
        for (Iterator<CardHasCardAttribute> it = card.getCardHasCardAttributeList().iterator(); it.hasNext();) {
            CardHasCardAttribute attr = it.next();
            attributes.put(attr.getCardAttribute().getName(), attr.getValue());
        }
        return attributes;
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
        } catch (Exception ex) {
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
                Card iCard = (Card) card;
                HashMap parameters = new HashMap();
                parameters.put("name", iCard.getName());
                if (namedQuery("Card.findByName", parameters).isEmpty()) {
                    createCard(iCard.getCardType(), iCard.getName(), iCard.getText());
                }
                return true;
            } catch (Exception ex) {
                Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    protected boolean doRemoveCard(T card) {
        if (card instanceof ICard) {
            try {
                ICard iCard = (ICard) card;
                HashMap parameters = new HashMap();
                parameters.put("name", iCard.getName());
                List result = namedQuery("Card.findByName", parameters);
                if (!result.isEmpty()) {
                    new CardJpaController(getEntityManagerFactory()).destroy(((Card) result.get(0)).getCardPK());
                    return true;
                }
            } catch (Exception ex) {
                Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (Exception ex) {
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
    public boolean cardTypeExists(String name) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            return namedQuery("CardType.findByName", parameters).isEmpty();
        } catch (Exception ex) {
            Logger.getLogger(DataBaseCardStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public String getCardAttribute(Card card, String name) {
        String result = null;
        for (CardHasCardAttribute chca : card.getCardHasCardAttributeList()) {
            if (chca.getCardAttribute().getName().equals(name)) {
                result = chca.getValue();
                break;
            }
        }
        return result;
    }
}
