package simple.server.extension.d20.map;

import marauroa.common.game.Definition;

public abstract class AbstractMap implements D20Map {

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }
}
