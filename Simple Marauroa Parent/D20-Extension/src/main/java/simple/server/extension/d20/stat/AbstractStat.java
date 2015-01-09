package simple.server.extension.d20.stat;

import marauroa.common.game.Definition;

public abstract class AbstractStat implements D20Stat {

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