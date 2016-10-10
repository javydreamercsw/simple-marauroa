package simple.server.extension.d20.deity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.DummyClass;
import simple.server.extension.d20.rpclass.D20Class;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = Deity.class)
public class DummyDeity extends AbstractDeity {

    @Override
    public String getCharacteristicName() {
        return "Big Dummy";
    }

    @Override
    public String getShortName() {
        return "Big Dummy";
    }

    @Override
    public String getDescription() {
        return "Big Dummy deity!";
    }

    @Override
    public List<Class<? extends D20Class>> getExclusiveClasses() {
        return Arrays.asList(DummyClass.class);
    }

    @Override
    public Map<Class<? extends D20Characteristic>, Integer> getRequirements() {
        return new HashMap<>();
    }

    @Override
    public Map<Class<? extends D20Characteristic>, Integer>
            getOpponentRequirements() {
        return new HashMap<>();
    }
}
