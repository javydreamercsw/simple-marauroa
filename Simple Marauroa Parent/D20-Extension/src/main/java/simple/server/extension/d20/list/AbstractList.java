package simple.server.extension.d20.list;

import marauroa.common.game.Definition;

public abstract class AbstractList implements D20List {

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }
}
