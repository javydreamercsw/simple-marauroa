package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Ability.class)
public class Strength extends AbstractAbility {

    public Strength() {
    }

    public Strength(int level) {
        super(level);
    }

    @Override
    public String getCharacteristicName() {
        return "Strength";
    }

    @Override
    public String getShortName() {
        return "STR";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }

    @Override
    public String getDescription() {
        return "Strength measures your character's muscle and physical power. "
                + "This ability is especially important for Power Houses and "
                + "Brawlers because it helps them prevail in combat. Strength "
                + "also limits how big opponents you can lift.";
    }
}
