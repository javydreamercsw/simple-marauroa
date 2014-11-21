package simple.server.extension.attribute;

/**
 * An attribute is something for a character like Wisdom, Fortitude, etc.
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */

public interface iD20Attribute extends iD20Characteristic{
    
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
