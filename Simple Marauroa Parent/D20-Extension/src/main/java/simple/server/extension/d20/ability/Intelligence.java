package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Ability.class)
public class Intelligence extends AbstractAbility {

    @Override
    public String getCharacteristicName() {
        return "Intelligence";
    }

    @Override
    public String getShortName() {
        return "INT";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }

    @Override
    public String getDescription() {
        return "Intelligence determines how well your character learns and "
                + "reasons. This ability is important for Strategists because "
                + "it affects their strategies, how hard their strategies are "
                + "to resist, and how powerful their strategies can be. "
                + "It's also important for any character who wants to have a "
                + "wide assortment of skills.";
    }
}
