package simple.server.extension;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.attribute.iD20Attribute;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = iD20Attribute.class)
public class DummyAttr2 implements iD20Attribute {

    @Override
    public String getName() {
        return "Dummy2";
    }

    @Override
    public String getShortName() {
        return "Dummy2";
    }

    @Override
    public int getAttributeMod() {
        return 0;
    }

    @Override
    public int getDefaultValue() {
        return 0;
    }

}
