package simple.server.extension.attribute;

/**
 * Stats like, HP
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface iD20Stat extends iD20Characteristic{
    public int getStatMod();

    public int getDefaultValue();
}
