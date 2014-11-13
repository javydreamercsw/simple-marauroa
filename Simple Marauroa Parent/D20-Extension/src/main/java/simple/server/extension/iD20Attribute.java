package simple.server.extension;

/**
 * An attribute is something for a character like Wisdom, Fortitude, etc.
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */

public interface iD20Attribute {
    /**
     * Get Attribute name.
     * @return Attribute name
     */
    public String getName();
    
    /**
     * Get the attribute id. Unique identification
     * @return attribute id
     */
    public String getID();
    
    /**
     * Get attribute modifier.
     * @return attribute modifier
     */
    public int getAttributeMod();
}
