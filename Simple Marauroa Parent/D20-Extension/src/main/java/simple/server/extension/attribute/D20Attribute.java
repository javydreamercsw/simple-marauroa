package simple.server.extension.attribute;

import simple.server.extension.iD20Definition;

/**
 * An attribute is something for a character like Wisdom, Fortitude, etc.
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */

public interface D20Attribute extends D20Characteristic, iD20Definition{
    
    /**
     * Get attribute modifier.
     * @return attribute modifier
     */
    public int getAttributeMod();
    
    /**
     * Get attribute default value.
     * @return default value
     */
    public String getDefaultValue();
}
