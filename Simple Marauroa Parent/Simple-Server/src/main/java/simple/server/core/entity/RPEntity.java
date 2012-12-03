package simple.server.core.entity;

import marauroa.common.Log4J;
import marauroa.common.Logger;
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

    private static final long serialVersionUID = 1L;
    /**
     * The title attribute name.
     */
    protected static final String ATTR_TITLE = "title";
    protected static final Statistics stats = Statistics.getStatistics();
    private int level;
    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(RPEntity.class);

    @Override
    public void generateRPClass() {
        try {
            if (!RPClass.hasRPClass("rpentity")) {
                RPClass entity = new RPClass("rpentity");
                entity.isA("entity");
                entity.addAttribute("name", Type.STRING);
                entity.addAttribute(ATTR_TITLE, Type.STRING);
            }
        } catch (SyntaxException e) {
            logger.error("cannot generateRPClass", e);
        }
    }

    public RPEntity(RPObject object) {
        super(object);
    }

    public RPEntity() {
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
