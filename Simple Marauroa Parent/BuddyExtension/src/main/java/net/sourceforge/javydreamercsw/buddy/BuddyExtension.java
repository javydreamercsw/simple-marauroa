package net.sourceforge.javydreamercsw.buddy;

import marauroa.common.game.Attributes;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.common.SimpleException;
import simple.common.game.ClientObjectInterface;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class BuddyExtension extends SimpleServerExtension {

    public static final String BUDDY = "!buddy", IGNORE = "!ignore",
            ONLINE = "online", OFFLINE = "offline";

    @Override
    public String getName() {
        return "Buddy Extension";
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        // We use this for the buddy system
        client.addRPSlot(BUDDY, 1, Definition.PRIVATE);
        client.addRPSlot(IGNORE, 1, Definition.HIDDEN);

        client.addAttribute(ONLINE, Definition.Type.LONG_STRING,
                (byte) (Definition.PRIVATE | Definition.VOLATILE));
        client.addAttribute(OFFLINE, Definition.Type.LONG_STRING,
                (byte) (Definition.PRIVATE | Definition.VOLATILE));

    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client)
            throws SimpleException {
        if (!((Attributes) client).has(ONLINE)) {
            ((Attributes) client).put(ONLINE, "");
        }
        if (!((Attributes) client).has(OFFLINE)) {
            ((Attributes) client).put(OFFLINE, "");
        }
    }
}
