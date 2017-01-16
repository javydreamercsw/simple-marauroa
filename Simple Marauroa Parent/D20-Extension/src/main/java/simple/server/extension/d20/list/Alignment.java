package simple.server.extension.d20.list;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20List.class)
public class Alignment extends AbstractList {

    public static final String ALIGNMENT = "Alignment";

    @Override
    public String getCharacteristicName() {
        return ALIGNMENT;
    }

    @Override
    public String getShortName() {
        return ALIGNMENT;
    }

    @Override
    public String getDescription() {
        return ALIGNMENT;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public Byte getDefinition() {
        return Definition.PRIVATE;
    }
}
