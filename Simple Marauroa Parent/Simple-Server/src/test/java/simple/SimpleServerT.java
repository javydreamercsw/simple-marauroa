package simple;

import marauroa.common.Log4J;
import org.junit.BeforeClass;
import org.openide.util.Lookup;
import simple.server.mock.MockSimpleRPWorld;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón<javier.ortiz.78@gmail.com>
 */
public abstract class SimpleServerT {

    @BeforeClass
    public static void setUpClass() {
        Log4J.init();

        MockSimpleRPWorld.get();

        Lookup.getDefault().lookupAll(RPEntityInterface.class).stream()
                .map((entity) -> {
                    System.out.println("Registering RPEntity: "
                            + entity.getClass().getSimpleName());
                    return entity;
                }).forEach((entity) -> {
                    entity.generateRPClass();
                });
    }

    public SimpleServerT() {
    }
}
