package simple.test;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 * Mock extension
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
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
}
