package simple.server.core.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Configuration;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.GameEventDAO;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPRuleProcessorImpl;
import marauroa.server.game.rp.RPServerManager;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.rp.SimpleRPAction;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.DelayedPlayerEventSender;
import simple.server.core.event.ILoginNotifier;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TurnNotifier;
import simple.server.core.tool.Tool;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IRPRuleProcessor.class)
public class SimpleRPRuleProcessor extends RPRuleProcessorImpl
        implements IRPRuleProcessor {

    /**
     * Current game name.
     */
    private String name;
    /**
     * Current game version.
     */
    private String version;
    /**
     * The Log instance.
     */
    private static final Logger LOG
            = Logger.getLogger(SimpleRPRuleProcessor.class.getSimpleName());
    //Current turn.
    private int turn = 0;

    public void addGameEvent(String source, String event, String... params) {
        try {
            DAORegister.get().get(GameEventDAO.class).addGameEvent(
                    source, event, params);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Can't store game event", e);
        }
    }

    public void setGameName(String name) {
        this.setName(name);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static SimpleRPRuleProcessor get() {
        return (SimpleRPRuleProcessor) Lookup.getDefault()
                .lookup(IRPRuleProcessor.class);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    @Override
    public void setContext(RPServerManager rpman) {
        try {
            /*
                 * Print version information.
             */
            LOG.log(Level.INFO, "Running {0} Server version ''{1}''",
                    new Object[]{getName(), getVersion()});
            super.setContext(rpman);
            SimpleRPAction.initialize(rpman);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Cannot set Context. Exiting...", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkGameVersion(String game, String version) {
        LOG.log(Level.FINE, "Comparing {0} (client) with {1} (server)",
                new Object[]{game, getName()});
        LOG.log(Level.FINE, "Comparing {0} (client) with {1} (server)",
                new Object[]{version, getVersion()});
        return game.equals(getName()) && version.equals(getVersion());
    }

    @Override
    public void execute(RPObject caster, RPAction action) {
        CommandCenter.execute(caster, action);
    }

    @Override
    public synchronized boolean onInit(RPObject object) {
        boolean result = true;
        Lookup.getDefault().lookup(IRPWorld.class).add(object);
        if (object.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
            final RPEntityInterface player = new RPEntity(object);
            try {
                addGameEvent(Tool.extractName(object), "login");
                Lookup.getDefault()
                        .lookupAll(ILoginNotifier.class).stream().forEach((ln)
                        -> {
                    ln.onPlayerLoggedIn(player);
                });
                welcome(player);
            } catch (Exception e) {
                LOG.log(Level.SEVERE,
                        "There has been a severe problem loading player "
                        + object.get("#db_id"), e);
                result = false;
            }
        }
        return result;
    }

    /**
     * Send a welcome message to the player which can be configured in
     * server.ini file as "server_welcome". If the value is an http:// address,
     * the first line of that address is read and used as the message
     *
     * @param player RPEntityInterface
     */
    protected static void welcome(final RPEntityInterface player) {
        String msg = "";
        try {
            Configuration config = Configuration.getConfiguration();
            if (config.has("server_welcome")) {
                msg = config.get("server_welcome");
                if (msg.startsWith("http://")) {
                    //Display from the url
                    URL url = new URL(msg);
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection connection
                            = (HttpURLConnection) url.openConnection();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        msg = br.readLine();
                    }
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        TurnNotifier notifier = Lookup.getDefault().lookup(TurnNotifier.class);
        if (msg != null && !msg.isEmpty()) {
            if (notifier != null) {
                notifier.notifyInTurns(10,
                        new DelayedPlayerEventSender(new PrivateTextEvent(
                                NotificationType.TUTORIAL, msg),
                                (RPObject) player));
            } else {
                LOG.log(Level.WARNING,
                        "Unable to send message: ''{0}'' to player: {1}",
                        new Object[]{msg, player.getName()});
            }
        }
    }

    @Override
    public synchronized void beginTurn() {
        super.beginTurn(); //Empty right now
        //Look for Turn Listeners and run them if applicable.
        Lookup.getDefault().lookupAll(ITurnNotifier.class).stream().map((tn) -> {
            turn++;
            return tn;
        }).forEachOrdered((tn) -> {
            tn.logic(turn);
        });
    }

    /**
     * Finds an online player with a specific name.
     *
     * @param name The player's name
     * @return The player, or null if no player with the given name is currently
     * online.
     */
    public RPObject getPlayer(String name) {
        RPObject player = null;
        for (IRPZone zone : Lookup.getDefault()
                .lookup(IRPWorld.class).getZones()) {
            if (zone instanceof ISimpleRPZone) {
                ISimpleRPZone sz = (ISimpleRPZone) zone;
                for (RPEntityInterface o : sz.getPlayers()) {
                    if (o.getName().equals(name)) {
                        player = (RPObject) o;
                        break;
                    }
                }
            }
        }
        return player;
    }

    /**
     * Finds an NPC.
     *
     * @param name NPC's name
     * @return The NPC, or null if not found.
     */
    public RPObject getNPC(String name) {
        RPObject npc = null;
        for (IRPZone zone : Lookup.getDefault()
                .lookup(IRPWorld.class).getZones()) {
            if (zone instanceof ISimpleRPZone) {
                ISimpleRPZone sz = (ISimpleRPZone) zone;
                for (RPObject o : sz.getNPCS()) {
                    if (Tool.extractName(o).equals(name)) {
                        npc = o;
                        break;
                    }
                }
            }
        }
        return npc;
    }
}
