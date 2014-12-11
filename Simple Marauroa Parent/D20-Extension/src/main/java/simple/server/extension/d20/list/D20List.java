package simple.server.extension.d20.list;

import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.iD20Definition;

/**
 * This represents lists, like skills, equipment, etc
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface D20List extends D20Characteristic, iD20Definition {

    /**
     * List size, use -1 for unlimited.
     *
     * @return list size.
     */
    public int getSize();
}
