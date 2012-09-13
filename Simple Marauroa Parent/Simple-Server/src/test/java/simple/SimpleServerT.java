package simple;

import marauroa.common.Log4J;
import org.openide.util.Lookup;
import simple.server.core.engine.MockSimpleRPWorld;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón<javier.ortiz.78@gmail.com>
 */
public abstract class SimpleServerT {

    public static void init() throws Exception {
        Log4J.init();

        MockSimpleRPWorld.get();

        for (RPEntityInterface entity : Lookup.getDefault().lookupAll(RPEntityInterface.class)) {
            entity.generateRPClass();
        }
    }

    public SimpleServerT() {
    }
}
