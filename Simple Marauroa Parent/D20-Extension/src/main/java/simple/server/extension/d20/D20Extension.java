package simple.server.extension.d20;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;
import simple.server.extension.d20.map.D20Map;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class D20Extension extends SimpleServerExtension {

    @Override
    public String getName() {
        return "D20 Extension";
    }

    @Override
    public void rootRPClassUpdate(RPObject entity) {
        if (entity.instanceOf(RPClass.getRPClass(AbstractClass.RP_CLASS))) {
            Lookup.getDefault().lookupAll(D20Map.class).stream().forEach((stat) -> {
                if (!entity.hasMap(stat.getName())) {
                    entity.addMap(stat.getName());
                }
            });
        }
    }
}
