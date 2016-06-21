package simple.server.extension.d20.list;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20List.class)
public class BonusSkillList extends AbstractList {

    public final static String NAME = "Bonus Skills",
            DESC = "List of bonus skills for this character.";

    @Override
    public int getSize() {
        return -1;
    }

    @Override
    public String getCharacteristicName() {
        return NAME;
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESC;
    }

    @Override
    public Byte getDefinition() {
        return Definition.PRIVATE;
    }
}
