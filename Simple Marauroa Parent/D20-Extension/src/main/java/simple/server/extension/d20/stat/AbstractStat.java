package simple.server.extension.d20.stat;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;

public abstract class AbstractStat extends RPObject implements D20Stat {

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }

    @Override
    public int getStatMod() {
        return 0;
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }
}
