package simple.server.extension.d20.damage;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface D20DamageProvider {

    /**
     * The damage equation for this entity. (i.e. 1d4+1)
     *
     * @return damage equation for this entity
     */
    public String getDamageEquation();
}
