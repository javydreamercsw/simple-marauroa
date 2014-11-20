package simple.server.extension;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.attribute.iD20List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = iD20List.class)
public class DummyList implements iD20List {

    @Override
    public String getName() {
        return DummyList.class.getSimpleName();
    }

    @Override
    public String getShortName() {
        return DummyList.class.getSimpleName();
    }

    @Override
    public int getSize() {
        return 10;
    }
}
