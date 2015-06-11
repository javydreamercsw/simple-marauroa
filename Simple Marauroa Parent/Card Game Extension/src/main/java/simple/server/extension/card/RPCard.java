package simple.server.extension.card;

import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardField;
import java.util.Date;
import java.util.UUID;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 1001)
public class RPCard extends RPEntity implements ICard {

    /**
     *
     */
    public final static String CLASS = "class",
            /**
             *
             */
            CARD_ID = "card_id",
            /**
             *
             */
            CREATION_DATE = "creation_date",
            /**
             *
             */
            TIMES_TRADED = "times_traded",
            /**
             *
             */
            TRADABLE = "tradable",
            /**
             *
             */
            CLASS_NAME = "card",
            /**
             *
             */
            SET = "set";
    private final InstanceContent content = new InstanceContent();
    /**
     * Lookup instance
     */
    private final Lookup lookup = new AbstractLookup(content);

    /**
     *
     */
    public RPCard() {
    }

    /**
     *
     * @param class0
     */
    public RPCard(Class class0) {
        setRPClass(CLASS_NAME);
        put("type", CLASS_NAME);
        put(CLASS, class0.getCanonicalName());
        put("name", class0.getSimpleName());
        update();
    }

    /**
     *
     */
    @Override
    public final void update() {
        super.update();
        //Unique id
        if (!has(CARD_ID)) {
            put(CARD_ID, UUID.randomUUID().toString());
        }
        //Creation date
        if (!has(CREATION_DATE)) {
            put(CREATION_DATE, new Date(System.currentTimeMillis()).toString());
        }
        //Times traded
        if (!has(TIMES_TRADED)) {
            put(TIMES_TRADED, 0);
        }
        //Tradable by default
        if (!has(TRADABLE)) {
            put(TRADABLE, "true");
        }
    }

    /**
     *
     * @return
     */
    public int getTimesTraded() {
        return getInt(TIMES_TRADED);
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(CLASS_NAME)) {
            RPClass entity = new RPClass(CLASS_NAME);
            entity.isA("entity");

            /**
             * Implementing class
             */
            entity.addAttribute(CLASS, Definition.Type.LONG_STRING);

            /**
             * Unique identifier
             */
            entity.addAttribute(CARD_ID, Definition.Type.LONG_STRING);

            /**
             * Date the card was created
             */
            entity.addAttribute(CREATION_DATE, Definition.Type.LONG_STRING);

            /**
             * Times traded
             */
            entity.addAttribute(TIMES_TRADED, Definition.Type.INT);

            /**
             * Can be traded?
             */
            entity.addAttribute(TRADABLE, Definition.Type.STRING);
        }
    }

    /**
     *
     */
    public void increaseTimesTraded() {
        if (getBool(TRADABLE)) {
            put(TIMES_TRADED, getTimesTraded() + 1);
        } else {
            throw new RuntimeException("Traded a non tradable card?");
        }
    }

    @Override
    public Object getObjectByField(ICardField field) {
        return get(field.name());
    }

    @Override
    public int getCardId() {
        return getInt(CARD_ID);
    }

    @Override
    public String getSetName() {
        return get(SET);
    }

    @Override
    public void setSetName(String set) {
        put(SET, set);
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && (!(o instanceof RPCard)
                || o.getClass() != getClass())) {
            return -1;
        }
        if (equals(o)) {
            return 0;
        } else {
            RPCard card = (RPCard) o;
            return card == null ? -1 : getName().compareTo(card.getName());
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Lookup getLookup() {
        return lookup;
    }

    /**
     *
     * @param entity
     */
    public void addToLookup(Object entity) {
        content.add(entity);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;
        if (obj == null) {
            result = false;
        }
        else if (getClass() != obj.getClass()) {
            result = false;
        }
        return result && super.equals(obj);
    }
}
