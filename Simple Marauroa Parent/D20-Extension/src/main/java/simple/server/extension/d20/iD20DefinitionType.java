package simple.server.extension.d20;

import marauroa.common.game.Definition;

public interface iD20DefinitionType {

    /**
     * The Attribute definition type.
     *
     * @see Definition.Type for options.
     * @return Attribute definition type.
     */
    Definition.Type getDefinitionType();
}
