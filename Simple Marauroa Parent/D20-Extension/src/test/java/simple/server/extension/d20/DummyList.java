package simple.server.extension.d20;

import simple.server.extension.d20.list.D20List;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.list.AbstractList;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20List.class)
public class DummyList extends AbstractList {

    @Override
    public String getCharacteristicName() {
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

    @Override
    public String getDescription() {
        return "Dummy";
    }
}
