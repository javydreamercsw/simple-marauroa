package simple.server.extension.d20.item;

import marauroa.common.game.Definition;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public abstract class AbstractItemAttribute implements D20ItemAttribute {

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.STRING;
    }
}
