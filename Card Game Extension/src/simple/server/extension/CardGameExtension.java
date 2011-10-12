package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;

/**
 * Generates the RPClasses needed for adding decks to players.
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class CardGameExtension extends SimpleServerExtension {

    public static final String DECK = "decks";

    @Override
    public void modifyClientObjectDefinition(RPClass player) {
        //Add decks to the player
        player.addRPSlot(DECK, -1, Definition.PRIVATE);
    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client) {
        super.clientObjectUpdate(client);
        if (!((RPObject) client).hasSlot(CardGameExtension.DECK)) {
            ((RPObject) client).addSlot(CardGameExtension.DECK);
        }
    }
}
