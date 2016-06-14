package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Ability.class)
public class Wisdom extends AbstractAbility {

    @Override
    public String getCharacteristicName() {
        return "Wisdom";
    }

    @Override
    public String getShortName() {
        return "WIS";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }

    @Override
    public String getDescription() {
        return "Wisdom describes a character's willpower, common sense, "
                + "perception, and intuition. While Intelligence represents "
                + "one's ability to analyze information, Wisdom represents "
                + "being in tune with and aware of one's surroundings. If you want "
                + "your character to have acute senses, put a high score in "
                + "Wisdom.";
    }
}
