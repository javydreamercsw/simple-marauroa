package simple.server.core.engine;

import java.util.List;
import marauroa.common.game.IRPZone;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface IDefaultZoneProvider {
    /**
     * List of RPZones to be created by default. the first returned is 
     * assumed to be the default zone
     * @return default Zones
     */
    List<IRPZone> getDefaultZones();
}
