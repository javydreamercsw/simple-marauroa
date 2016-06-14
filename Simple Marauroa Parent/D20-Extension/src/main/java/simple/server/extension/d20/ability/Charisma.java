package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Ability.class)
public class Charisma extends AbstractAbility {

    @Override
    public String getCharacteristicName() {
        return "Charisma";
    }

    @Override
    public String getShortName() {
        return "CHA";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }

    @Override
    public String getDescription() {
        return "Charisma measures a character's force of personality, "
                + "persuasiveness, personal magnetism, ability to lead, "
                + "and physical attractiveness. This ability represents "
                + "actual strength of personality, not merely how one is "
                + "perceived by others in a social setting.";
    }
}
