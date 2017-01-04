package simple.server.core.entity.character;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.entity.ExtensibleRPClass;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.extension.MarauroaServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProviders({
    @ServiceProvider(service = ClientObjectInterface.class)
    ,@ServiceProvider(service = RPEntityInterface.class, position = 101)})
public class PlayerCharacter extends ClientObject {

    public static final String DEFAULT_RP_CLASSNAME = "player_character";
    /**
     * the LOG instance.
     */
    private static final Logger LOG
            = Logger.getLogger(PlayerCharacter.class.getSimpleName());

    public PlayerCharacter(RPObject object) {
        super(object);
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
        setRPClass(RPCLASS_NAME);
        put(WellKnownActionConstant.TYPE, RPCLASS_NAME);
        awayReplies = new HashMap<>();
    }

    public PlayerCharacter() {
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
    }

    /**
     * Generates the SimpleRPClass and specifies slots and attributes.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            ExtensibleRPClass player = new ExtensibleRPClass(getRPClassName());
            player.isA(ClientObject.DEFAULT_RP_CLASSNAME);
            extendClass(player);
        }
    }

    protected static void extendClass(RPClass player) {
        Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                .stream().map((extension) -> {
                    LOG.log(Level.FINE, "Processing extension to modify "
                            + "client definition: {0}",
                            extension.getClass().getSimpleName());
                    return extension;
                }).forEach((extension) -> {
            extension.modifyCharacterRPClassDefinition(player);
        });
        LOG.fine("ClientObject attributes:");
        player.getDefinitions().stream().forEach((def) -> {
            LOG.log(Level.FINE, "{0}: {1}",
                    new Object[]{def.getName(), def.getType()});
        });
        LOG.fine("-------------------------------");
    }
}
