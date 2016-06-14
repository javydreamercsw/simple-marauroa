package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Ability.class)
public class Constitution extends AbstractAbility {

    @Override
    public String getCharacteristicName() {
        return "Constitution";
    }

    @Override
    public String getShortName() {
        return "CON";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }

    @Override
    public String getDescription() {
        return "Constitution represents your character's health and stamina. "
                + "A Constitution bonus increases a character's hit points, "
                + "so the ability is important for all classes.";
    }
}
