package simple.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class DummyExtension extends SimpleServerExtension {

    public static final String testAttr1 = "attr1";
    public static final String testAttr2 = "attr2";
    public static final String testAttr3 = "attr3";

    @Override
    public void modifyRootRPClassDefinition(RPClass entity) {
        entity.addAttribute(testAttr1, Definition.Type.INT);
    }

    @Override
    public void rootRPClassUpdate(RPObject client) {
        if (!client.has(testAttr1)) {
            client.put(testAttr1, 1);
        }
    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client) {
        if (!((RPObject) client).has(testAttr2)) {
            ((RPObject) client).put(testAttr2, 1);
        }
        if (!((RPObject) client).has(testAttr3)) {
            ((RPObject) client).put(testAttr3, 1);
        }
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        client.addAttribute(testAttr3, Definition.Type.INT);
    }
    
    

    public String getName() {
        return "Dummy";
    }
}
