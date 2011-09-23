package utilities.RPClass;

import marauroa.common.game.RPClass;
import simple.server.core.entity.Entity;

public class EntityTestHelper {

    public static void generateRPClasses() {

        if (!RPClass.hasRPClass("entity")) {
            Entity.generateRPClass();
        }
    }
}
