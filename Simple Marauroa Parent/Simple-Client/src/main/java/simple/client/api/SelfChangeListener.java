package simple.client.api;

import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface SelfChangeListener {

    public boolean onMyRPObject(RPObject added, RPObject deleted);
}
