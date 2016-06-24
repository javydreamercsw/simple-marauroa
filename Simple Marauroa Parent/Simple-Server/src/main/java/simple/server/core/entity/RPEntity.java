package simple.server.core.entity;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;
import marauroa.server.game.Statistics;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 2)
public class RPEntity extends Entity {

    /**
     * The title attribute name.
     */
    public static final String ATTR_TITLE = "title";
    private final String MY_CLASS = "rpentity";
    protected static final Statistics STATS = Statistics.getStatistics();
    private int level;
    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(RPEntity.class.getSimpleName());

    @Override
    public void generateRPClass() {
        super.generateRPClass();
        try {
            if (!RPClass.hasRPClass(MY_CLASS)) {
                RPClass entity = new RPClass(MY_CLASS);
                entity.isA(Entity.class.newInstance().getRPClassName());
                entity.addAttribute(NAME, Type.STRING);
                entity.addAttribute(ATTR_TITLE, Type.STRING);
            } else if (!RPClass.hasRPClass(getRPClassName())) {
                RPClass entity = new RPClass(getRPClassName());
                entity.isA(MY_CLASS);
            }
        } catch (SyntaxException e) {
            LOG.log(Level.SEVERE, "Cannot generateRPClass", e);
        } catch (InstantiationException | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public RPEntity(RPObject object) {
        super(object);
    }

    public RPEntity() {
        RPCLASS_NAME = "rpentity";
    }

    /**
     * Gets this RPEntity's outfit.
     *
     * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't use
     * the outfit system.
     *
     * @return The outfit, or null if this RPEntity is represented as a single
     * sprite rather than an outfit combination.
     */
    @Override
    public Outfit getOutfit() {
        return has("outfit") ? new Outfit(getInt("outfit")) : null;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
        put("level", level);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setOutfit(Outfit o, boolean defaultValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
