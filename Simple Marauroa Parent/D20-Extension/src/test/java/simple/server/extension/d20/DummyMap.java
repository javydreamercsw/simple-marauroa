package simple.server.extension.d20;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.map.AbstractMap;
import simple.server.extension.d20.map.D20Map;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Map.class)
public class DummyMap extends AbstractMap {

    @Override
    public String getName() {
        return "Dummy-Map";
    }

    @Override
    public String getShortName() {
        return "DMap";
    }

    @Override
    public String getDescription() {
        return "Dummy Map";
    }
}
