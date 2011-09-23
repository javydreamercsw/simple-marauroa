package simple.client.extension;

import java.util.HashMap;
import java.util.Map;
import marauroa.common.Log4J;
import marauroa.common.Logger;

public abstract class SimpleClientExtension implements ClientExtension {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(SimpleClientExtension.class);
    /** Lists the instances of the loaded extensions. */
    private static Map<String, SimpleClientExtension> loadedInstances = new HashMap<String, SimpleClientExtension>();

    @Override
    public synchronized boolean perform(String name) {
        return (false);
    }

    @Override
    public String getMessage(String name) {
        return (null);
    }

    public static SimpleClientExtension getInstance(String name) {
        try {
            Class<?> extensionClass = Class.forName(name);

            if (!SimpleClientExtension.class.isAssignableFrom(extensionClass)) {
                logger.debug("Class is no instance of SimpleServerExtension: " + name);
                return null;
            }

            logger.info("Loading ClientExtension: " + name);
            java.lang.reflect.Constructor<?> constr = extensionClass.getConstructor();

            // simply create a new instance. The constructor creates all
            // additionall objects
            SimpleClientExtension instance = (SimpleClientExtension) constr.newInstance();
            // store it in the hashmap for later reference
            loadedInstances.put(name, instance);
            return instance;
        } catch (Exception e) {
            logger.warn("SimpleClientExtension " + name + " loading failed.",
                    e);
            return null;
        }
    }

    public static boolean isLoaded(String name) {
        return loadedInstances.containsKey(name);
    }
}
