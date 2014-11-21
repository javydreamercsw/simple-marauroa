package simple.server.extension;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.ability.AbstractAbility;
import simple.server.extension.ability.D20Ability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Ability.class)
public class DummyAbility extends AbstractAbility {

    private static int counter = 0;

    @Override
    public String getName() {
        return "Dummy";
    }

    @Override
    public String getShortName() {
        return "Dummy";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.values()[counter++];
    }
}
