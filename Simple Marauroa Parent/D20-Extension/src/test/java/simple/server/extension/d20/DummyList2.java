package simple.server.extension.d20;

import simple.server.extension.d20.list.D20List;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.list.AbstractList;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20List.class)
public class DummyList2 extends AbstractList {

    @Override
    public String getCharacteristicName() {
        return DummyList2.class.getSimpleName();
    }

    @Override
    public String getShortName() {
        return DummyList2.class.getSimpleName();
    }

    @Override
    public int getSize() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Dummy";
    }
}
