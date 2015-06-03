package simple.server.extension.d20.alignment;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;

public class AbstractAlignment extends RPObject implements D20Alignment {

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
