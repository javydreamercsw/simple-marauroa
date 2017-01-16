package simple.test;

import marauroa.common.game.Attributes;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 * Mock extension
 *
 * @author Javier A. Ortiz BultrÃ³n javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class TestExtension extends SimpleServerExtension {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public void modifyItemRPClassDefinition(RPClass item) {
        item.addAttribute("1", Definition.Type.STRING);
    }

    @Override
    public void modifyRootEntityRPClassDefinition(RPClass root) {
        root.addAttribute("2", Definition.Type.STRING);
    }

    @Override
    public void modifyRootRPClassDefinition(RPClass client) {
        client.addAttribute("3", Definition.Type.STRING);
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        client.addAttribute("4", Definition.Type.STRING);
    }

    @Override
    public void modifyCharacterRPClassDefinition(RPClass clazz) {
        clazz.addAttribute("5", Definition.Type.STRING);
    }

    @Override
    public void rootRPClassUpdate(RPObject client) {
        if (!client.has("3")) {
            client.put("3", 1);
        }
    }

    @Override
    public void characterRPClassUpdate(RPObject client) {
        if (!client.has("5")) {
            client.put("5", 1);
        }
    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client) {
        if (!((Attributes) client).has("4")) {
            ((Attributes) client).put("4", 1);
        }
    }

    @Override
    public void itemRPClassUpdate(RPObject item) {
        if (!item.has("1")) {
            item.put("1", 1);
        }
    }
}
