package simple.server.extension.d20;

import simple.server.extension.d20.D20List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20List.class)
public class DummyList implements D20List {

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
    
    @Override
    public String getDescription() {
        return "Dummy";
    }
}