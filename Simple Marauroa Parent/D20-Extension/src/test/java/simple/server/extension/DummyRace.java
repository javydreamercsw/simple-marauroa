package simple.server.extension;

import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.attribute.iD20Race;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProviders({
    @ServiceProvider(service = iD20Race.class),
    @ServiceProvider(service = RPEntityInterface.class)})
public class DummyRace extends AbstractRace {

    public DummyRace() {
        RPCLASS_NAME="Dummy_Race";
    }

    public DummyRace(RPObject object) {
        super(object);
    }

    @Override
    public String getShortName() {
        return "Dummy Race";
    }
}
