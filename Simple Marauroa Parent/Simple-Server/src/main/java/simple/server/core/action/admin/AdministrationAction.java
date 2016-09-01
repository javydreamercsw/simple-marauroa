package simple.server.core.action.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.entity.Entity;

/**
 * Most /commands for admins are handled here.
 */
public abstract class AdministrationAction implements ActionProvider {

    private static final String TARGETID = "targetid";
    protected static final Logger LOG
            = Logger.getLogger(AdministrationAction.class.getSimpleName());
    public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPPORT = 100;
    public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPER = 5000;
    protected static final Map<String, Integer> REQUIRED_ADMIN_LEVELS
            = new HashMap<>();

    static {
        REQUIRED_ADMIN_LEVELS.put("support", 100);
        REQUIRED_ADMIN_LEVELS.put("super", 5000);
    }

    public static void registerCommandLevel(String command, int minLevel) {
        REQUIRED_ADMIN_LEVELS.put(command, minLevel);
    }

    public static Integer getLevelForCommand(String command) {
        Integer val = REQUIRED_ADMIN_LEVELS.get(command);
        if (val == null) {
            val = -1;
        }
        return val;
    }

    public static boolean isPlayerAllowedToExecuteAdminCommand(ClientObjectInterface player,
            String command, boolean verbose) {
        // get adminlevel of player and required adminlevel for this command
        int adminlevel = player.getAdminLevel();
        Integer required = REQUIRED_ADMIN_LEVELS.get(command);

        // check that we know this command
        if (required == null) {
            LOG.log(Level.SEVERE, "Unknown command {0}", command);
            if (verbose) {
                player.sendPrivateText("Sorry, command \"" + command + "\" is unknown.");
            }
            return false;
        }

        if (adminlevel < required) {
            // not allowed
            LOG.log(Level.WARNING,
                    "Player {0} with admin level {1} tried to run admin "
                    + "command {2} which requires level {3}.",
                    new Object[]{player.getName(), adminlevel, command,
                        required});

            // Notify the player if verbose is set.
            if (verbose) {

                // is this player an admin at all?
                if (adminlevel == 0) {
                    player.sendPrivateText("Sorry, you need to be an admin to "
                            + "run \"" + command + "\".");
                } else {
                    player.sendPrivateText("Your admin level is only "
                            + adminlevel + ", but a level of " + required
                            + " is required to run \"" + command + "\".");
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            String type = action.get(WellKnownActionConstant.TYPE);
            if (!isPlayerAllowedToExecuteAdminCommand(player, type, true)) {
                return;
            }
            perform(player, action);
        }
    }

    protected abstract void perform(ClientObjectInterface player,
            RPAction action);

    /**
     * get the Entity-object of the specified target.
     *
     * @param player
     * @param action
     * @return the Entity or null if it does not exist
     */
    protected final Entity getTarget(ClientObjectInterface player,
            RPAction action) {

        String id = null;
        Entity target = null;

        // target contains a name unless it starts with #
        if (action.has(WellKnownActionConstant.TARGET)) {
            id = action.get(WellKnownActionConstant.TARGET);
        }
        if (id != null) {
            if (id.startsWith("#")) {
                id = id.substring(1);
            } else {
                target = (Entity) ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class))
                        .getPlayer(id);
                return target;
            }
        }

        // either target started with a # or it was not specified
        if (action.has(TARGETID)) {
            id = action.get(TARGETID);
        }

        // go for the id
        if (id != null) {
            SimpleRPZone zone = player.getZone();

            RPObject.ID oid = new RPObject.ID(Integer.parseInt(id),
                    zone.getName());
            if (zone.has(oid)) {
                RPObject object = zone.get(oid);
                if (object instanceof Entity) {
                    target = (Entity) object;
                }
            }
        }

        return target;
    }
}
