
package simple.server.core.entity;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;
import marauroa.server.game.Statistics;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RPEntity extends Entity{

    private static final long serialVersionUID = 1L;
    /**
     * The title attribute name.
     */
    protected static final String ATTR_TITLE = "title";
    protected static Statistics stats;
    private int level;
    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(RPEntity.class);

    static{
        RPCLASS_NAME = "rpentity";
    }

    public static void generateRPClass() {
        try {
            stats = Statistics.getStatistics();
            RPClass entity = new RPClass(RPCLASS_NAME);
            entity.isA("entity");
            entity.addAttribute("name", Type.STRING);
            entity.addAttribute(ATTR_TITLE, Type.STRING);
            entity.addAttribute("level", Type.SHORT);
            entity.addAttribute("xp", Type.INT);

            entity.addAttribute("base_hp", Type.SHORT);
            entity.addAttribute("hp", Type.SHORT);

            entity.addAttribute("atk", Type.SHORT, Definition.PRIVATE);
            entity.addAttribute("def", Type.SHORT, Definition.PRIVATE);
            entity.addAttribute("atk_item", Type.INT,
                    (byte) (Definition.PRIVATE | Definition.VOLATILE));
            entity.addAttribute("def_item", Type.INT,
                    (byte) (Definition.PRIVATE | Definition.VOLATILE));

            entity.addAttribute("risk", Type.BYTE, Definition.VOLATILE);
            entity.addAttribute("damage", Type.INT, Definition.VOLATILE);
            entity.addAttribute("heal", Type.INT, Definition.VOLATILE);
            entity.addAttribute("title_type", Type.STRING, Definition.VOLATILE);

            entity.addRPSlot("head", 1, Definition.PRIVATE);
            entity.addRPSlot("rhand", 1, Definition.PRIVATE);
            entity.addRPSlot("lhand", 1, Definition.PRIVATE);
            entity.addRPSlot("belt", 1, Definition.PRIVATE);
            entity.addRPSlot("legs", 1, Definition.PRIVATE);
            entity.addRPSlot("feet", 1, Definition.PRIVATE);
        } catch (SyntaxException e) {
            logger.error("cannot generateRPClass", e);
        }
    }

    public RPEntity(RPObject object) {
        super(object);
    }

    public RPEntity() {
        super();
    }

    /**
     * Gets this RPEntity's outfit.
     * 
     * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't use
     * the outfit system.
     * 
     * @return The outfit, or null if this RPEntity is represented as a single
     *         sprite rather than an outfit combination.
     */
    @Override
    public Outfit getOutfit() {
        if (has("outfit")) {
            return new Outfit(getInt("outfit"));
        }
        return null;
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
