package simple.server.extension.attribute;

/**
 * This represents lists, like skills, equipment, etc
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface iD20List {
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
     * List size, use -1 for unlimited.
     * @return list size.
     */
    public int getSize();
}
