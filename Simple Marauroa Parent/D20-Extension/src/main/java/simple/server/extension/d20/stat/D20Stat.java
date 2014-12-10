package simple.server.extension.d20.stat;

import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.iD20Definition;

/**
 * Stats like, HP
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface D20Stat extends D20Characteristic, iD20Definition{
    public int getStatMod();

    public int getDefaultValue();
}
