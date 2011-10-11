package simple.server.extension;

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

    public final static String CARD_NAME = "card_name", CLASS = "class",
            CARD_ID = "card_id", CREATION_DATE = "creation_date",
            TIMES_TRADED = "times_traded", TRADABLE = "tradable";

    @Override
    public void generateRPClass() {
        RPClass entity = new RPClass(RPCLASS_NAME);
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
        entity.addAttribute(TRADABLE, Definition.Type.BYTE);
    }
}
