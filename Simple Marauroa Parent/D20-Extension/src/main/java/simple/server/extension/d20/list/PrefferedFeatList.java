package simple.server.extension.d20.list;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20List.class)
public class PrefferedFeatList extends AbstractList {

    public final static String NAME = "Preffered Feats",
            DESC = "List of preffered feats for this character.";

    @Override
    public String getCharacteristicName() {
        return NAME;
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESC;
    }

    @Override
    public Byte getDefinition() {
        return Definition.PRIVATE;
    }
}
