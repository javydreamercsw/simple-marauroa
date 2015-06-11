package simple.server.extension.d20;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;
import simple.server.extension.d20.misc.AbstractMisc;
import simple.server.extension.d20.misc.D20Misc;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20Misc.class)
public class DummyMisc extends AbstractMisc {

    @Override
    public String getDescription() {
        return "Dummy-Misc";
    }

    @Override
    public Byte getDefinition() {
        return Definition.STANDARD;
    }

    @Override
    public Definition.Type getDefinitionType() {
        return Definition.Type.STRING;
    }

}
