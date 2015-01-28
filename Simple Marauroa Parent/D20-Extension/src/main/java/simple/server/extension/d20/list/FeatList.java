package simple.server.extension.d20.list;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20List.class)
public class FeatList extends AbstractList {

    public static final String FEAT = "feats";

    @Override
    public int getSize() {
        return -1;
    }

    @Override
    public String getCharacteristicName() {
        return FEAT;
    }

    @Override
    public String getShortName() {
        return FEAT;
    }

    @Override
    public String getDescription() {
        return "List of feats for this wrestler.";
    }

    @Override
    public Byte getDefinition() {
        return Definition.PRIVATE;
    }
}
