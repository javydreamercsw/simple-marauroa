package simple.server.extension;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Stat.class)
public class DummyStat implements D20Stat {

    @Override
    public String getName() {
        return "DummyStat";
    }

    @Override
    public String getShortName() {
        return "DummyStat";
    }

    @Override
    public int getStatMod() {
        return 0;
    }

    @Override
    public String getDefaultValue() {
        return "0";
    }
    
    private static int counter = 0;

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.values()[counter++];
    }
}
