package simple.server.core.engine;

import java.util.Iterator;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.event.api.IRPEvent;
import utilities.RPClass.ItemTestHelper;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class MockSimpleRPWorld extends SimpleRPWorld {

    @Override
    public void modify(final RPObject object) {
    }

    @Override
    protected void createRPClasses() {
        if (!RPClass.hasRPClass("entity")) {
            Entity.generateRPClass();
        }

        if (!RPClass.hasRPClass("rpentity")) {
            RPEntity.generateRPClass();
        }

        if (!RPClass.hasRPClass("client_object")) {
            Lookup.getDefault().lookup(IRPObjectFactory.class).generateClientObjectRPClass();
        }

        for (Iterator<? extends IRPEvent> it = Lookup.getDefault().lookupAll(IRPEvent.class).iterator(); it.hasNext();) {
            IRPEvent event = it.next();
            event.generateRPClass();
        }
        ItemTestHelper.generateRPClasses();
    }

    @Override
    protected void initialize() {
    }
}
