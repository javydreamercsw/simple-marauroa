package net.sourceforge.javydreamercsw.outfit.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.entity.Entity;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class OutfitExtension extends SimpleServerExtension {

    public static final String OUTFIT = "outfit", ORIGINAL_OUTFIT = "outfit_org";

    @Override
    public String getName() {
        return "Outfit Extension";
    }

    @Override
    public void modifyCharacterRPClassDefinition(RPClass character) {
        super.modifyCharacterRPClassDefinition(character);
        character.addAttribute(OUTFIT, Definition.Type.INT);//Character's outfit
        character.addAttribute(ORIGINAL_OUTFIT, Definition.Type.INT);//Allows for temporary outfit.
    }

    /**
     * Makes this player wear the given outfit. If the given outfit contains
     * null parts, the current outfit will be kept for these parts.
     *
     * @param target Object to add the outfit to.
     * @param outfit The new outfit.
     * @param temporary If true, the original outfit will be stored so that it
     * can be restored later.
     */
    public static void setOutfit(RPObject target, Outfit outfit, boolean temporary) {
        // if the new outfit is temporary and the player is not wearing
        // a temporary outfit already, store the current outfit in a
        // second slot so that we can return to it later.
        if (temporary && !target.has(ORIGINAL_OUTFIT)) {
            target.put(ORIGINAL_OUTFIT, target.get(OUTFIT));
        }

        // if the new outfit is not temporary, remove the backup
        if (!temporary && target.has(ORIGINAL_OUTFIT)) {
            target.remove(ORIGINAL_OUTFIT);
        }

        // combine the old outfit with the new one, as the new one might
        // contain null parts.
        Outfit newOutfit = outfit.putOver(getOutfit(target));
        target.put(OUTFIT, newOutfit.getCode());
        ((Entity) target).notifyWorldAboutChanges();
    }

    /**
     * Get original outfit in case it's using a temporary one.
     *
     * @param target target to get outfit from.
     * @return The outfit, or null if this RPEntity is represented as a single
     * sprite rather than an outfit combination.
     */
    public static Outfit getOriginalOutfit(RPObject target) {
        return target.has(ORIGINAL_OUTFIT)
                ? new Outfit(target.getInt(ORIGINAL_OUTFIT)) : null;
    }

    /**
     * Set outfit to a target
     *
     * @param target target to set outfit to.
     * @param outfit Outfit to set
     */
    public static void setOutfit(RPObject target, Outfit outfit) {
        setOutfit(target, outfit, false);
    }

    /**
     * Gets this RPEntity's outfit.
     *
     * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't use
     * the outfit system.
     *
     * @param target Target to get outfit from.
     * @return The outfit, or null if this RPEntity is represented as a single
     * sprite rather than an outfit combination.
     */
    public static Outfit getOutfit(RPObject target) {
        return target.has(OUTFIT) ? new Outfit(target.getInt(OUTFIT)) : null;
    }
}
