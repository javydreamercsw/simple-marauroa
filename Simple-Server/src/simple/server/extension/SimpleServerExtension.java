package simple.server.extension;


import marauroa.common.game.Perception;
import marauroa.common.game.RPObject;
import marauroa.server.game.extension.MarauroaServerExtension;

public abstract class SimpleServerExtension extends MarauroaServerExtension{
    
    /**
     * Query the extension to plug in any changes to the perception of an object.
     * This is called after the normal perceptions are sent.
     * @param object Object to potentially modify the perception
     */
    
    public boolean updateMonitor(RPObject object, Perception perception) {
        return true;
    }
}
