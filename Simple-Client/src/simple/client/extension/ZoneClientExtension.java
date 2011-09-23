package simple.client.extension;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPEvent;
import simple.client.event.listener.RPEventListener;
import simple.server.core.event.ZoneEvent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 * Add the following to extension.xml (at games.jwrestling.client.conf)
 * <extension name= "Zone" class="games.jwrestling.client.extension.ZoneClientExtension">
 *      <dependencies>
 *          <RPEvent name="ZoneEvent" class="games.jwrestling.server.core.event.ZoneEvent"/>
 *          <Extension name="Zone">
 *              <type>
 *                  <server></server>
 *              </type>
 *          </Extension>
 *      </dependencies>
 * </extension>
 */
public class ZoneClientExtension extends SimpleClientExtension implements RPEventListener {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(ZoneClientExtension.class);

    /**
     *
     */
    @Override
    public void init() {
        //TODO: Register
    }

    /**
     *
     * @param event
     */
    @Override
    public void onRPEventReceived(RPEvent event) {
        logger.debug("Room event received: " + event);
        int action = event.getInt(ZoneEvent.getAction());
        switch (action) {
            case ZoneEvent.ADD:
                //TODO
                break;
            case ZoneEvent.REMOVE:
                //TODO
                break;
            case ZoneEvent.UPDATE:
                //TODO
                break;
            case ZoneEvent.LISTZONES:
                //TODO
                break;
            case ZoneEvent.NEEDPASS:
                //TODO
                break;
            default:
                logger.debug("Invalid room action: " + action);
        }
       //TODO
    }
}
