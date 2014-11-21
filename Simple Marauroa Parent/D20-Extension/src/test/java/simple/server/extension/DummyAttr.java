package simple.server.extension;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.attribute.iD20Attribute;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = iD20Attribute.class)
public class DummyAttr implements iD20Attribute {

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
}
