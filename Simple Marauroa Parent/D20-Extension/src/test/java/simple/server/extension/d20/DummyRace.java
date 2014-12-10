package simple.server.extension.d20;

import simple.server.extension.d20.race.D20Race;
import simple.server.extension.d20.AbstractRace;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProviders({
    @ServiceProvider(service = D20Race.class),
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

    @Override
    public String getHPDice() {
        return "0";
    }
}
