package simple.server.extension.d20.alignment;

import marauroa.common.game.Definition;

public class AbstractAlignment implements D20Alignment {

    @Override
    public String getCharacteristicName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getShortName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }
}
