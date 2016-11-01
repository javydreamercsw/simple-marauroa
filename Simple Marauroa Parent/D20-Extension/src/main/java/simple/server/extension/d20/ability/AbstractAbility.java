package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;
import simple.server.extension.d20.level.D20Level;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractAbility extends RPObject implements D20Ability {

    public AbstractAbility() {
        super(new RPObject());
        put(D20Level.LEVEL, 0);
    }

    public AbstractAbility(int level) {
        super(new RPObject());
        put(D20Level.LEVEL, level);
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }
}
