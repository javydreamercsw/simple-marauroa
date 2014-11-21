package simple.server.extension.attribute;

import marauroa.common.game.Definition;

public interface iD20Definition {

    /**
     * The Attribute definition type.
     *
     * @see Definition.Type for options.
     * @return Attribute definition type.
     */
    Definition.Type getDefinitionType();
}
