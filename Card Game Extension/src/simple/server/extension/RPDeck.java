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
@ServiceProvider(service = RPEntityInterface.class, position = 1002)
public class RPDeck extends RPEntity {

    @Override
    public void generateRPClass() {
        RPClass entity = new RPClass("deck");
        entity.isA("entity");

        /**
         * RPCards
         */
        entity.addRPSlot("pages", Definition.PRIVATE);

        /**
         * Starting hand
         */
        entity.addRPLink("hand", Definition.PRIVATE);
    }
}
