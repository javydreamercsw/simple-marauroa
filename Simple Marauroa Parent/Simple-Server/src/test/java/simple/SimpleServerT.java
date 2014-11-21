package simple;

import marauroa.common.Log4J;
import org.openide.util.Lookup;
import simple.server.mock.MockSimpleRPWorld;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón<javier.ortiz.78@gmail.com>
 */
public abstract class SimpleServerT {

    public static void init() throws Exception {
        Log4J.init();

        MockSimpleRPWorld.get();

        Lookup.getDefault().lookupAll(RPEntityInterface.class).stream().forEach((entity) -> {
            System.out.println("Initializing RPEntity: " + entity.getClass().getSimpleName());
            entity.generateRPClass();
        });
    }

    public SimpleServerT() {
    }
}
