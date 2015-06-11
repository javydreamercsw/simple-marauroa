package simple.server.extension.d20;

/**
 * Shared methods of all characteristics.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Characteristic {

    /**
     * Get Attribute name.
     *
     * @return Attribute name
     */
    public String getCharacteristicName();

    /**
     * Get the attribute id. Unique identification
     *
     * @return attribute id
     */
    public String getShortName();

    /**
     * Description of the characteristic.
     *
     * @return description.
     */
    public String getDescription();
}
