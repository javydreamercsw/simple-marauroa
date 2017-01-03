package simple.server.core.engine;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.GameEventDAO;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPRuleProcessorImpl;
import marauroa.server.game.rp.RPServerManager;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.rp.SimpleRPAction;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.ILoginNotifier;
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
        Lookup.getDefault().lookup(IRPWorld.class).add(object);
        return result;
    }

    /**
     * Welcome the player to the world.
     *
     * @param player Player to welcome
     */
    private void welcome(RPEntityInterface player) {

    }
}
