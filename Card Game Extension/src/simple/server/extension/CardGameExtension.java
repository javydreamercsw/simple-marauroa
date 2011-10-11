package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates the RPClasses needed for adding decks to players.
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class CardGameExtension extends SimpleServerExtension {

    @Override
    public void modifyClientObjectDefinition(RPClass player) {
        //Add decks to the player
        player.addRPSlot("decks", Definition.PRIVATE);
    }
}
