package simple.server.extension;

import marauroa.common.game.Definition;
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
public class GagExtension extends SimpleServerExtension {

    private final String GAG = "gag";

    @Override
    public String getName() {
        return "Gag Extension";
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        client.addAttribute(GAG, Definition.Type.INT);
    }
    
    @Override
    public void clientObjectUpdate(ClientObjectInterface client) throws SimpleException {
        if (!((RPObject) client).has(GAG)) {
            ((RPObject) client).add(GAG, 0);
        }
    }
}
