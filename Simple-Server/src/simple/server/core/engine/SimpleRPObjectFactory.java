package simple.server.core.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.RPObjectFactory;
import simple.common.game.ClientObjectInterface;

/**
 * Creates concrete objects of simple classes.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SimpleRPObjectFactory extends RPObjectFactory {

    private static Logger logger = Log4J.getLogger(SimpleRPObjectFactory.class);
    protected static SimpleRPObjectFactory singleton;

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

    /**
     * Returns the factory instance (this method is called
     * by Marauroa using reflection).
     *
     * @return RPObjectFactory
     */
    public static SimpleRPObjectFactory getFactory() {
        if (singleton == null) {
            singleton = new SimpleRPObjectFactory();
        }
        return singleton;
    }

    public static void generateClientObjectRPClass() {
        try {
            Configuration conf = Configuration.getConfiguration();
            if (conf.get("client_object") != null && !conf.get("client_object").isEmpty()) {
                logger.info("Using " + conf.get("client_object") + " as client object class.");
                Class<?> clientObjectClass = Class.forName(conf.get("client_object"));
                java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("generateRPClass");
                localSingleton.invoke(null);
            }
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void destroyClientObject(ClientObjectInterface object) {
        try {
            Configuration conf = Configuration.getConfiguration();
            Class<?> clientObjectClass = Class.forName(conf.get("client_object"));
            Class[] types = new Class[]{clientObjectClass};
            java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("destroy", types);
            localSingleton.invoke(clientObjectClass, object);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ClientObjectInterface createClientObject(RPObject object) {
        try {
            Configuration conf = Configuration.getConfiguration();
            Class<?> clientObjectClass = Class.forName(conf.get("client_object"));
            Class[] types = new Class[]{RPObject.class};
            java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("create", types);
            return (ClientObjectInterface) localSingleton.invoke(clientObjectClass, object);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (InvocationTargetException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NoSuchMethodException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SecurityException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SimpleRPObjectFactory.class.getSimpleName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static ClientObjectInterface createDefaultClientObject(String name) {
        try {
            Configuration conf = Configuration.getConfiguration();
            Class<?> clientObjectClass = Class.forName(conf.get("client_object"));
            Class[] types = new Class[]{String.class};
            java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("createDefaultClientObject", types);
            return (ClientObjectInterface) localSingleton.invoke(clientObjectClass, name);
        } catch (IllegalAccessException ex) {
            logger.error(ex);
            return null;
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
            return null;
        } catch (InvocationTargetException ex) {
            logger.error(ex);
            return null;
        } catch (NoSuchMethodException ex) {
            logger.error(ex);
            return null;
        } catch (SecurityException ex) {
            logger.error(ex);
            return null;
        } catch (ClassNotFoundException ex) {
            logger.error(ex);
            return null;
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
    }

    public static ClientObjectInterface createDefaultClientObject(RPObject entity) {
        try {
            Configuration conf = Configuration.getConfiguration();
            Class<?> clientObjectClass = Class.forName(conf.get("client_object"));
            Class[] types = new Class[]{RPObject.class};
            java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("createDefaultObject", types);
            return (ClientObjectInterface) localSingleton.invoke(clientObjectClass, entity);
        } catch (IllegalAccessException ex) {
            logger.error(ex);
            return null;
        } catch (IllegalArgumentException ex) {
            logger.error(ex);
            return null;
        } catch (InvocationTargetException ex) {
            logger.error(ex);
            return null;
        } catch (NoSuchMethodException ex) {
            logger.error(ex);
            return null;
        } catch (SecurityException ex) {
            logger.error(ex);
            return null;
        } catch (ClassNotFoundException ex) {
            logger.error(ex);
            return null;
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }
    }
}
