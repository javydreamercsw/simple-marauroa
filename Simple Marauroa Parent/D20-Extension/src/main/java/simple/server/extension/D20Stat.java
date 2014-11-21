package simple.server.extension;

/**
 * Stats like, HP
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface D20Stat extends D20Characteristic, iD20Definition{
    public int getStatMod();

    public int getDefaultValue();
}
