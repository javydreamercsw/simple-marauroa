package simple.server.core.engine;

import java.util.ArrayList;
import java.util.List;
import marauroa.common.game.IRPZone;
import org.openide.util.lookup.ServiceProvider;

/**
 * Default Zone provider
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDefaultZoneProvider.class)
public class DefaultZoneProvider implements IDefaultZoneProvider {

    private ArrayList<IRPZone> zones = new ArrayList<IRPZone>();

    @Override
    public List<IRPZone> getDefaultZones() {
        return getZones();
    }

    /**
     * @return the zones
     */
    public ArrayList<IRPZone> getZones() {
        return zones;
    }
}
