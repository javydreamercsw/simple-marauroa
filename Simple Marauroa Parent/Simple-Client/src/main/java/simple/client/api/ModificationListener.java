package simple.client.api;

import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ModificationListener {

    public boolean onModifiedAdded(RPObject object, RPObject changes);

    public boolean onModifiedDeleted(RPObject object, RPObject changes);
}
