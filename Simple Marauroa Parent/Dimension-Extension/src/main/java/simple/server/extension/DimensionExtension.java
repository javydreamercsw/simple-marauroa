package simple.server.extension;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class DimensionExtension extends SimpleServerExtension {

    @Override
    public void modifyRootRPClassDefinition(RPClass entity) {
        /**
         * Resistance to other entities (0-100). 0=Phantom, 100=Obstacle.
         */
        entity.addAttribute("resistance", Definition.Type.BYTE, Definition.VOLATILE);
        /**
         * Entity location
         */
        entity.addAttribute("x", Definition.Type.SHORT);
        entity.addAttribute("y", Definition.Type.SHORT);
        entity.addAttribute("z", Definition.Type.SHORT);
        /*
         * The size of the entity (in world units).
         */
        entity.addAttribute("width", Definition.Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("height", Definition.Type.SHORT, Definition.VOLATILE);
        /*
         * The visibility of the entity drawn on client (0-100). 0=Invisible,
         * 100=Solid. Useful when mixed with effect.
         */
        entity.addAttribute("visibility", Definition.Type.INT, Definition.VOLATILE);
    }

    @Override
    public void rootRPClassUpdate(RPObject entity) {
        if (!entity.has("width")) {
            entity.put("width", 1);
        }
        if (!entity.has("height")) {
            entity.put("height", 1);
        }
        if (!entity.has("x")) {
            entity.put("x", 0);
        }
        if (!entity.has("y")) {
            entity.put("y", 0);
        }
        if (!entity.has("z")) {
            entity.put("z", 0);
        }
        if (!entity.has("resistance")) {
            entity.put("resistance", 100);
        }
        if (!entity.has("visibility")) {
            entity.put("visibility", 100);
        }
    }

    public String getName() {
        return "Dimension Extension";
    }
}
