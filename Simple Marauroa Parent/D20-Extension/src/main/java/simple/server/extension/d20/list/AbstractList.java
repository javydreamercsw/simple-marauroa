package simple.server.extension.d20.list;

import marauroa.common.game.Definition;

public abstract class AbstractList implements D20List {

    @Override
    public int getSize() {
        return -1;
    }

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }
}
