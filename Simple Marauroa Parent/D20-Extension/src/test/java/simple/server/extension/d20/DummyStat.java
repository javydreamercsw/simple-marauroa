package simple.server.extension.d20;

import simple.server.extension.d20.stat.D20Stat;
import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.stat.AbstractStat;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Stat.class)
public class DummyStat extends AbstractStat {

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
    public int getDefaultValue() {
        return 0;
    }

    private static int counter = 0;

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.values()[counter++];
    }

    @Override
    public String getDescription() {
        return "Dummy";
    }
}
