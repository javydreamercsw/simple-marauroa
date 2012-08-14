package simple.server.core.engine;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.RPObjectFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;

/**
 * Creates concrete objects of simple classes.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IRPObjectFactory.class)
public class SimpleRPObjectFactory extends RPObjectFactory implements IRPObjectFactory {

    private static Logger logger = Log4J.getLogger(SimpleRPObjectFactory.class);

    @Override
    public RPObject transform(RPObject object) {
        RPClass clazz = object.getRPClass();
        if (clazz == null) {
            logger.error("Cannot create concrete object for " + object
                    + " because it does not have an SimpleRPClass.");
            return super.transform(object);
        }
        // fallback
        return super.transform(object);
    }

    public SimpleRPObjectFactory() {
    }
    
    /**
     * Returns the factory instance (this method is called
     * by Marauroa using reflection).
     *
     * @return RPObjectFactory
     */
    public static SimpleRPObjectFactory getFactory() {
        return (SimpleRPObjectFactory) Lookup.getDefault().lookup(IRPObjectFactory.class);
    }

    @Override
    public void destroyClientObject(ClientObjectInterface object) {
        Lookup.getDefault().lookup(ClientObjectInterface.class).destroy();
    }

    @Override
    public ClientObjectInterface createClientObject(RPObject object) {
        return Lookup.getDefault().lookup(ClientObjectInterface.class).create(object);
    }

    @Override
    public ClientObjectInterface createDefaultClientObject(String name) {
        return Lookup.getDefault().lookup(ClientObjectInterface.class).createDefaultClientObject(name);
    }

    @Override
    public ClientObjectInterface createDefaultClientObject(RPObject entity) {
        return Lookup.getDefault().lookup(ClientObjectInterface.class).createDefaultClientObject(entity);
    }
}
