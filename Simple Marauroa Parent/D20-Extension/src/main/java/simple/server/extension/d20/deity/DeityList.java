package simple.server.extension.d20.deity;

import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.list.AbstractList;
import simple.server.extension.d20.list.D20List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = D20List.class)
public class DeityList extends AbstractList {

    public static final String DEITY = "Deity";

    @Override
    public String getCharacteristicName() {
        return DEITY;
    }

    @Override
    public String getShortName() {
        return DEITY;
    }

    @Override
    public String getDescription() {
        return "TODO";
    }

    @Override
    public int getSize() {
        return 1;
    }
}
