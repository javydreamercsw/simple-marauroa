package simple.server.core.engine;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;
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
            SimpleRPObjectFactory.generateClientObjectRPClass();
        }

        if (!RPClass.hasRPClass(PrivateTextEvent.getRPClassName())) {
            PrivateTextEvent.generateRPClass();
        }

        if (!RPClass.hasRPClass(TextEvent.getRPClassName())) {
            TextEvent.generateRPClass();
        }
        ItemTestHelper.generateRPClasses();
    }

    public static SimpleRPWorld get() {
        try {
            if (!(instance instanceof MockSimpleRPWorld)) {
                instance = new MockSimpleRPWorld();
                ((MockSimpleRPWorld) instance).createRPClasses();
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void initialize() {
    }

    public static void reset() {
        instance = null;
    }
}
