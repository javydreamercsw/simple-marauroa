package net.sourceforge.javydreamercsw.admin;

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
public class AdminExtension extends SimpleServerExtension {

    public static final String ADMIN = "admin", ADMIN_LEVEL = "adminlevel";

    @Override
    public String getName() {
        return "Admin Extension";
    }

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        client.addAttribute(ADMIN, Definition.Type.FLAG);
        client.addAttribute(ADMIN_LEVEL, Definition.Type.INT);
    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client)
            throws SimpleException {
        if (!((Attributes) client).has(ADMIN)) {
            ((Attributes) client).put(ADMIN, 0);
        }
        if (!((Attributes) client).has(ADMIN_LEVEL)) {
            ((Attributes) client).put(ADMIN_LEVEL, -1);
        }
    }
}
