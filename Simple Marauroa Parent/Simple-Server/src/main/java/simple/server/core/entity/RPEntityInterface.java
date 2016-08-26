package simple.server.core.entity;

import simple.common.Constants;
import simple.server.core.engine.SimpleRPZone;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RPEntityInterface extends Constants {

    /**
     * Called when this object is added to a zone.
     *
     * @param zone The zone this was added to.
     */
    public void onAdded(SimpleRPZone zone);

    /**
     * Get current zone;
     *
     * @return current zone
     */
    public SimpleRPZone getZone();

    /**
     * Action(s) to take when removed from a zone.
     *
     * @param zone
     */
    public void onRemoved(SimpleRPZone zone);

    /**
     * Generate this RPClass
     */
    public void generateRPClass();
}
