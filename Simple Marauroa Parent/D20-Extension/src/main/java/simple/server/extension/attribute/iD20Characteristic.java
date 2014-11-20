package simple.server.extension.attribute;

/**
 * Shared methods of all characteristics.
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface iD20Characteristic {
    /**
     * Get Attribute name.
     * @return Attribute name
     */
    public String getName();
    
    /**
     * Get the attribute id. Unique identification
     * @return attribute id
     */
    public String getShortName();
}
