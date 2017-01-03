package simple.server.core.engine;

import java.util.ArrayList;
import java.util.List;
import marauroa.common.game.IRPZone;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Default Zone provider
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IDefaultZoneProvider.class)
public class DefaultZoneProvider implements IDefaultZoneProvider {

    private final ArrayList<IRPZone> ZONES = new ArrayList<>();

    public DefaultZoneProvider() {
        IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);
        if (world != null) {
            IRPZone defaultZone = world.getDefaultZone();
            if (defaultZone != null) {
                ZONES.add(defaultZone);
            }
        }
    }

    @Override
    public List<IRPZone> getDefaultZones() {
        return getZONES();
    }

    /**
     * @return the ZONES
     */
    public ArrayList<IRPZone> getZONES() {
        return ZONES;
    }
}
