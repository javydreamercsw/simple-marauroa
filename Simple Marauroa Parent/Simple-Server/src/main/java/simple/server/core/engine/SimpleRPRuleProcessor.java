package simple.server.core.engine;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Configuration;
import marauroa.common.Pair;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.Statistics;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.GameEventDAO;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPRuleProcessorImpl;
import marauroa.server.game.rp.RPServerManager;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.Debug;
import simple.common.filter.FilterCriteria;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.CommandCenter;
import simple.server.core.action.admin.AdministrationAction;
import simple.server.core.engine.rp.SimpleRPAction;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.ILoginNotifier;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.tool.Tool;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IRPRuleProcessor.class)
public class SimpleRPRuleProcessor extends RPRuleProcessorImpl
        implements IRPRuleProcessor {

    private Configuration config;
    private static String VERSION;
    private static String GAMENAME;
    private static boolean log_chat = false;
    /**
     * the LOG instance.
     */
    private static final Logger LOG
            = Logger.getLogger(SimpleRPRuleProcessor.class.getSimpleName());
    protected static RPServerManager rpman;
    protected PlayerList onlinePlayers;
    /**
     * A list of RPEntities that were killed in the current turn, together with
     * the Entity that killed it.
     */
    private final List<Pair<RPEntity, Entity>> entityToKill;

    public PlayerList getOnlinePlayers() {
        return onlinePlayers;
    }

    public SimpleRPRuleProcessor() {
        try {
            config = Configuration.getConfiguration();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        onlinePlayers = new PlayerList();
        entityToKill = new LinkedList<>();
        setVERSION(config.get("server_version"));
        setGAMENAME(config.get("server_name"));
        setLogChat("true".equals(config.get("log_chat", "true")));
        addGameEvent("server system", "startup");
    }

    /**
     * @return the VERSION
     */
    public static String getVERSION() {
        return VERSION;
    }

    /**
     * @param aVERSION the VERSION to set
     */
    public static void setVERSION(String aVERSION) {
        VERSION = aVERSION;
    }

    /**
     * @return the GAMENAME
     */
    public static String getGAMENAME() {
        return GAMENAME;
    }

    /**
     * @param aGAMENAME the GAMENAME to set
     */
    public static void setGAMENAME(String aGAMENAME) {
        GAMENAME = aGAMENAME;
    }

    public static SimpleRPRuleProcessor get() {
        return (SimpleRPRuleProcessor) Lookup.getDefault()
                .lookup(IRPRuleProcessor.class);
    }

    public final void addGameEvent(String source, String event, String... params) {
        try {
            DAORegister.get().get(GameEventDAO.class).addGameEvent(
                    source, event, params);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Can't store game event", e);
        }
    }

    @Override
    public void setContext(RPServerManager rpman) {
        if (SimpleRPRuleProcessor.rpman == null) {
            try {
                /*
                 * Print version information.
                 */
                LOG.log(Level.INFO, "Running {0} Server version ''{1}''",
                        new Object[]{getGAMENAME(), getVERSION()});
                SimpleRPRuleProcessor.rpman = rpman;
                SimpleRPAction.initialize(rpman);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Cannot set Context. Exiting...", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean checkGameVersion(String game, String version) {
        LOG.fine("Comparing " + game + " (client) with " + getGAMENAME()
                + " (server)");
        LOG.fine("Comparing " + version + " (client) with " + getVERSION()
                + " (server)");
        return game.equals(getGAMENAME()) && version.equals(getVERSION());
    }

    public void killRPEntity(RPEntity entity, Entity killer) {
        entityToKill.add(new Pair<>(entity, killer));
    }

    /**
     * Checks whether the given RPEntity has been killed this turn.
     *
     * @param entity The entity to check.
     * @return true if the given entity has been killed this turn.
     */
    private boolean wasKilled(RPEntity entity) {
        return entityToKill.stream().anyMatch((entry)
                -> (entity.equals(entry.first())));
    }

    /**
     * Finds an online player with a specific name.
     *
     * @param name The player's name
     * @return The player, or null if no player with the given name is currently
     * online.
     */
    public ClientObjectInterface getPlayer(String name) {
        return getOnlinePlayers().getOnlinePlayer(name);
    }

    /**
     * Finds an NPC.
     *
     * @param name NPC's name
     * @return The NPC, or null if not found.
     */
    public RPEntityInterface getNPC(String name) {
        RPEntityInterface npc = null;
        for (SimpleRPZone zone : Lookup.getDefault().lookup(IRPWorld.class).getZones()) {
            for (RPEntityInterface o : zone.getNPCS()) {
                if (Tool.extractName((RPObject) o).equals(name)) {
                    npc = o;
                    break;
                }
            }
        }
        return npc;
    }

    @Override
    public boolean onActionAdd(RPObject caster, RPAction action,
            List<RPAction> actionList) {
        return true;
    }

    @Override
    public void execute(RPObject caster, RPAction action) {
        if (caster instanceof ClientObjectInterface) {
            CommandCenter.execute((ClientObjectInterface) caster, action);
        } else {
            LOG.severe(caster + " tried to execute action: " + action);
        }
    }

    public int getTurn() {
        return rpman.getTurn();
    }

    /**
     * Notify it when a new turn happens.
     */
    @Override
    public synchronized void beginTurn() {
        debugOutput();
        try {
            logNumberOfPlayersOnline();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error in beginTurn.", e);
        }
    }

    protected void logNumberOfPlayersOnline() {
        // We keep the number of players logged.
        Statistics.getStatistics().set("Players logged",
                getOnlinePlayers().size());
    }

    private void debugOutput() {
        /*
         * Debug statement for inspecting list of things. Most of our memories
         * leaks came from list keep adding and adding elements.
         */
        if (Debug.SHOW_LIST_SIZES && (rpman.getTurn() % 1000 == 0)) {
            int objects = 0;

            for (IRPZone zone : Lookup.getDefault().lookup(IRPWorld.class)) {
                objects += zone.size();
            }

            StringBuilder os = new StringBuilder();
            os.append("entityToKill: ").append(entityToKill.size())
                    .append("\n");
            os.append("players: ").append(getOnlinePlayers().size())
                    .append("\n");
            os.append("objects: ").append(objects).append("\n");
            LOG.info(os.toString());
        }
    }

    @Override
    public synchronized void endTurn() {
        int currentTurn = getTurn();
        try {
            for (IRPZone zoneI : Lookup.getDefault().lookup(IRPWorld.class)) {
                if (zoneI instanceof SimpleRPZone) {
                    SimpleRPZone zone = (SimpleRPZone) zoneI;
                    zone.logic();
                }
            }
            // Run registered object's logic method for this turn
            Lookup.getDefault().lookup(ITurnNotifier.class).logic(currentTurn);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error in endTurn", e);
        }
    }

    /**
     * send the content of the zone the player is in to the client.
     *
     * @param player
     */
    public static void transferContent(final ClientObjectInterface player) {
        final SimpleRPZone zone = player.getZone();
        rpman.transferContent((RPObject) player, zone.getContents());
    }

    @Override
    public synchronized boolean onInit(RPObject object) {
        boolean result = true;
        try {
            final PlayerEntry entry
                    = PlayerEntryContainer.getContainer().get(object);
            final ClientObjectInterface player
                    = Lookup.getDefault().lookup(IRPObjectFactory.class)
                    .createClientObject(object);
            entry.object = (RPObject) player;

            Lookup.getDefault().lookup(IRPWorld.class)
                    .addPlayer((RPObject) player);
            addGameEvent(player.getName(), "login");
            Lookup.getDefault()
                    .lookupAll(ILoginNotifier.class).stream().forEach((ln) -> {
                ln.onPlayerLoggedIn(player);
            });

            getOnlinePlayers().add(player);
            if (!player.isGhost()) {
                notifyOnlineStatus(true, player.getName());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "There has been a severe problem loading player "
                    + object.get("#db_id"), e);
            result = false;
        }
        return result;
    }

    @Override
    public synchronized boolean onExit(RPObject object) {
        super.onExit(object);
        try {
            ClientObjectInterface player = ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class))
                    .getPlayer(object.get("name"));
            if (player != null) {
                if (wasKilled((RPEntity) player)) {
                    LOG.info("Logged out shortly before death: "
                            + "Killing it now :)");
                }
                if (!player.isGhost()) {
                    notifyOnlineStatus(false, player.getName());
                }
                Lookup.getDefault().lookup(IRPObjectFactory.class)
                        .destroyClientObject(player);
                getOnlinePlayers().remove(player);

                //Player is still somewhere else?
                Iterator it = SimpleRPWorld.get().iterator();
                while (it.hasNext()) {
                    SimpleRPZone zone = (SimpleRPZone) it.next();
                    if (zone.has(((RPObject) player).getID())
                            && !zone.getName().equals(
                                    player.getZone().getName())) {
                        LOG.log(Level.WARNING,
                                "Another instance of the player found in {0}",
                                zone.getName());
                        zone.remove(((RPObject) player).getID());
                    }
                }
                addGameEvent(player.getName(), "logout");

                LOG.log(Level.FINE, "removed player {0}", player);
            }
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error in onExit.", e);
            return true;
        }
    }

    @Override
    public synchronized void onTimeout(RPObject object) {
        onExit(object);
    }

    public RPServerManager getRPManager() {
        return rpman;
    }

    /**
     * Tell this message all players.
     *
     * @param message Message to tell all players
     */
    public void tellAllPlayers(final String message) {
        getOnlinePlayers().tellAllOnlinePlayers(message);
    }

    /**
     * sends a message to all supporters.
     *
     * @param message Support message
     */
    public static void sendMessageToSupporters(final String message) {
        ((SimpleRPRuleProcessor) Lookup.getDefault()
                .lookup(IRPRuleProcessor.class)).getOnlinePlayers()
                .forFilteredPlayersExecute(
                        new Task<ClientObjectInterface>() {
                    @Override
                    public void execute(ClientObjectInterface player) {
                        player.sendPrivateText(message);
                        player.notifyWorldAboutChanges();
                    }
                },
                        new FilterCriteria<ClientObjectInterface>() {
                    @Override
                    public boolean passes(ClientObjectInterface p) {
                        return p.getAdminLevel()
                                >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT;
                    }
                });
    }

    /**
     * sends a message to all supporters.
     *
     * @param source a player or script name
     * @param message Support message
     */
    public static void sendMessageToSupporters(final String source,
            final String message) {
        final String text = source + " asks for support to ADMIN: " + message;
        sendMessageToSupporters(text);
    }

    public static int getAmountOfOnlinePlayers() {
        return ((SimpleRPRuleProcessor) Lookup.getDefault()
                .lookup(IRPRuleProcessor.class)).getOnlinePlayers().size();
    }

    public static void notifyOnlineStatus(boolean isOnline, final String name) {
        if (isOnline) {
            ((SimpleRPRuleProcessor) Lookup.getDefault()
                    .lookup(IRPRuleProcessor.class)).getOnlinePlayers()
                    .forAllPlayersExecute(new Task<ClientObjectInterface>() {
                        @Override
                        public void execute(ClientObjectInterface player) {
                            player.notifyOnline(name);
                        }
                    });

        } else {
            ((SimpleRPRuleProcessor) Lookup.getDefault()
                    .lookup(IRPRuleProcessor.class)).getOnlinePlayers()
                    .forAllPlayersExecute(new Task<ClientObjectInterface>() {
                        @Override
                        public void execute(ClientObjectInterface player) {
                            player.notifyOffline(name);
                        }
                    });
        }
    }

    /**
     * @return the log_chat
     */
    public static boolean isLogChat() {
        return log_chat;
    }

    /**
     * @param aLog_chat the log_chat to set
     */
    public static void setLogChat(boolean aLog_chat) {
        log_chat = aLog_chat;
    }
}
