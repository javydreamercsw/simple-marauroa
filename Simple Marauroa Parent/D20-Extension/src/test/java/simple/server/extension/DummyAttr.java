package simple.server.extension;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.attribute.D20Attribute;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20Attribute.class)
public class DummyAttr implements D20Attribute {

    private static int counter = 0;

    @Override
    public String getName() {
        return "Dummy";
    }

    @Override
    public String getShortName() {
        return "Dummy";
    }

    @Override
    public int getAttributeMod() {
        return 0;
    }

    @Override
    public String getDefaultValue() {
        return "0";
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.values()[counter++];
    }
}
