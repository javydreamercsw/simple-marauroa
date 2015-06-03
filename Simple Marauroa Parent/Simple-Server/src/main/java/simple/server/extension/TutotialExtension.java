package simple.server.extension;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.common.SimpleException;
import simple.common.game.ClientObjectInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class TutotialExtension extends SimpleServerExtension {

    private static final String TUTORIAL = "!tutorial";

    @Override
    public String getName() {
        return "Tutorial Extension";
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        client.addRPSlot(TUTORIAL, -1);
    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client) throws SimpleException {
        if (!((RPObject) client).hasSlot(TUTORIAL)) {
            ((RPObject) client).addSlot(TUTORIAL);
        }
    }
}
