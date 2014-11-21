package simple.server.extension.attribute;

import simple.server.extension.iD20Definition;

/**
 * Stats like, HP
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface D20Stat extends D20Characteristic, iD20Definition{
    public int getStatMod();

    public String getDefaultValue();
}
