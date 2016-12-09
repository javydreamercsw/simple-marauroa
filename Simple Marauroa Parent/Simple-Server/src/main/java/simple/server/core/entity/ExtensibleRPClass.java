package simple.server.core.entity;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import org.openide.util.Lookup;
import simple.server.extension.MarauroaServerExtension;

/**
 * Extensible RPClass
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class ExtensibleRPClass extends RPClass {

    private static final Logger LOG
            = Logger.getLogger(ExtensibleRPClass.class.getName());

    public ExtensibleRPClass(String RPCLASS_NAME) {
        super(RPCLASS_NAME);
    }

    public ExtensibleRPClass() {
    }

    @Override
    public void addAttribute(String name, Type type, byte b) {
        for (MarauroaServerExtension extension : Lookup.getDefault()
                .lookupAll(MarauroaServerExtension.class)) {
            LOG.log(Level.FINE,
                    "Processing extension to add attribute: {0}",
                    extension.getClass().getSimpleName());
            extension.onRPClassAddAttribute(this, name, type, b);
        }
        super.addAttribute(name, type, b);
    }

    @Override
    public void addAttribute(String name, Type type) {
        for (MarauroaServerExtension extension : Lookup.getDefault()
                .lookupAll(MarauroaServerExtension.class)) {
            LOG.log(Level.FINE,
                    "Processing extension to add attribute: {0}",
                    extension.getClass().getSimpleName());
            extension.onRPClassAddAttribute(this, name, type);
        }
        super.addAttribute(name, type);
    }
}
