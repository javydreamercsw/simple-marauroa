package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Ability.class)
public class Dexterity extends AbstractAbility {

    public Dexterity() {
    }

    public Dexterity(int level) {
        super(level);
    }

    @Override
    public String getCharacteristicName() {
        return "Dexterity";
    }

    @Override
    public String getShortName() {
        return "DEX";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.INT;
    }

    @Override
    public String getDescription() {
        return "Dexterity measures hand-eye coordination, agility, reflexes, "
                + "and balance. This ability is the most important one for "
                + "High Flyers, but it's also high on the list for characters "
                + "who typically low on Strenght.";
    }
}
