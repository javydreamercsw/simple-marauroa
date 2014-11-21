package simple.server.extension;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.attribute.iD20Stat;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = iD20Stat.class)
public class DummyStat implements iD20Stat {

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
}
