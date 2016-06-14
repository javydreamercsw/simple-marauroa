package simple.server.extension.d20;

import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.d20.rpclass.AbstractClass;
import simple.server.extension.d20.rpclass.D20Class;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProviders({
    @ServiceProvider(service = D20Class.class),
    @ServiceProvider(service = RPEntityInterface.class)})
public class DummyClass extends AbstractClass {

    public DummyClass() {
        super(new RPObject());
        RPCLASS_NAME = "Dummy_Class";
    }

    public DummyClass(RPObject object) {
        super(object);
    }

    @Override
    public String getShortName() {
        return "Dummy Class";
    }

    @Override
    public String getHPDice() {
        return "0";
    }

    @Override
    public String getCharacteristicName() {
        return "Dummy Class";
    }
}
