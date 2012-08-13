package simple.server.core.entity;

import simple.common.Constants;
import simple.server.core.engine.SimpleRPZone;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RPEntityInterface extends Constants {

    int getLevel();

    /**
     * Gets this RPEntity's outfit.
     *
     * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't use
     * the outfit system.
     *
     * @return The outfit, or null if this RPEntity is represented as a single
     * sprite rather than an outfit combination.
     */
    Outfit getOutfit();

    void setLevel(int level);

    void setOutfit(Outfit o, boolean defaultValue);

    /**
     * Called when this object is added to a zone.
     *
     * @param zone
     *            The zone this was added to.
     */
    public void onAdded(SimpleRPZone zone);

    public SimpleRPZone getZone();

    public void onRemoved(SimpleRPZone zone);
    /**
     * Generate this RPClass
     */
    public void generateRPClass();
}
