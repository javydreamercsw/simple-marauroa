package simple.server.core.entity;

import java.util.Iterator;
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

    public ExtensibleRPClass(String RPCLASS_NAME) {
        super(RPCLASS_NAME);
    }

    public ExtensibleRPClass() {
    }

    @Override
    public void addAttribute(String name, Type type, byte b) {
        for (Iterator<? extends MarauroaServerExtension> it = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it.hasNext();) {
            MarauroaServerExtension extension = it.next();
            Logger.getLogger(ExtensibleRPClass.class.getSimpleName()).log(Level.FINE,
                    "Processing extension to add attribute: {0}",
                    extension.getClass().getSimpleName());
            extension.onRPClassAddAttribute(this, name, type, b);
        }
        super.addAttribute(name, type, b);
    }

    @Override
    public void addAttribute(String name, Type type) {
        for (Iterator<? extends MarauroaServerExtension> it = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it.hasNext();) {
            MarauroaServerExtension extension = it.next();
            Logger.getLogger(ExtensibleRPClass.class.getSimpleName()).log(Level.FINE,
                    "Processing extension to add attribute: {0}",
                    extension.getClass().getSimpleName());
            extension.onRPClassAddAttribute(this, name, type);
        }
        super.addAttribute(name, type);
    }
}
