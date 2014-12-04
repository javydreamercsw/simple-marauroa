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

    public static final String X = "x", Y = "y", Z = "z",
            RESISTANCE = "resistance", WIDTH = "width",
            HEIGHT = "height", VISIBILITY = "visibility";

    @Override
    public void modifyRootRPClassDefinition(RPClass entity) {
        /**
         * Resistance to other entities (0-100). 0=Phantom, 100=Obstacle.
         */
        entity.addAttribute(RESISTANCE, Definition.Type.BYTE,
                Definition.VOLATILE);
        /**
         * Entity location
         */
        entity.addAttribute(X, Definition.Type.SHORT);
        entity.addAttribute(Y, Definition.Type.SHORT);
        entity.addAttribute(Z, Definition.Type.SHORT);
        /*
         * The size of the entity (in world units).
         */
        entity.addAttribute(WIDTH, Definition.Type.SHORT, Definition.VOLATILE);
        entity.addAttribute(HEIGHT, Definition.Type.SHORT, Definition.VOLATILE);
        /*
         * The visibility of the entity drawn on client (0-100). 0=Invisible,
         * 100=Solid. Useful when mixed with effect.
         */
        entity.addAttribute(VISIBILITY, Definition.Type.INT,
                Definition.VOLATILE);
    }

    @Override
    public void rootRPClassUpdate(RPObject entity) {
        if (!entity.has(WIDTH)) {
            entity.put(WIDTH, 1);
        }
        if (!entity.has(HEIGHT)) {
            entity.put(HEIGHT, 1);
        }
        if (!entity.has(X)) {
            entity.put(X, 0);
        }
        if (!entity.has(Y)) {
            entity.put(Y, 0);
        }
        if (!entity.has(Z)) {
            entity.put(Z, 0);
        }
        if (!entity.has(RESISTANCE)) {
            entity.put(RESISTANCE, 100);
        }
        if (!entity.has(VISIBILITY)) {
            entity.put(VISIBILITY, 100);
        }
    }

    public String getName() {
        return "Dimension Extension";
    }
}
