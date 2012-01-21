package simple.server.extension;

import java.util.Date;
import java.util.UUID;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 1001)
public class RPCard extends RPEntity {

    public final static String CLASS = "class",
            CARD_ID = "card_id", CREATION_DATE = "creation_date",
            TIMES_TRADED = "times_traded", TRADABLE = "tradable", CLASS_NAME = "card";

    public RPCard() {
    }

    public RPCard(Class class0) {
        setRPClass(CLASS_NAME);
        put("type", CLASS_NAME);
        put(CLASS, class0.getCanonicalName());
        put("name", class0.getSimpleName());
        update();
    }

    @Override
    public void update() {
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

    public int getTimesTraded() {
        return getInt(TIMES_TRADED);
    }

    @Override
    public void generateRPClass() {
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

    public void increaseTimesTraded() {
        if (getBool(TRADABLE)) {
            put(TIMES_TRADED, getTimesTraded() + 1);
        } else {
            throw new RuntimeException("Traded a non tradable card?");
        }
    }
}