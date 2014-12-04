package simple.server.extension.d20;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.ability.AbstractAbility;
import simple.server.extension.d20.ability.D20Ability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Ability.class)
public class DummyAbility2 extends AbstractAbility {

    @Override
    public String getName() {
        return "Dummy2";
    }

    @Override
    public String getShortName() {
        return "Dummy2";
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    private static int counter = 0;

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.values()[counter++];
    }
    
    @Override
    public String getDescription() {
        return "Dummy";
    }
}
