package simple.server.extension.d20.ability;

import marauroa.common.game.Definition;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractAbility implements D20Ability {

    protected int score = 0;

    @Override
    public int getDefaultValue() {
        return 0;
    }

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }
}
