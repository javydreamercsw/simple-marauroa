package simple.server.extension.d20.misc;

import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.iD20Definition;
import simple.server.extension.d20.iD20DefinitionType;

/**
 * Allows to define miscellaneous fields.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Misc extends D20Characteristic, iD20Definition,
        iD20DefinitionType {
    
    public String getDefaultValue();
}
