/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.server.db.TransactionPool;
import simple.server.application.db.SimpleDatabase;
import simple.server.core.entity.clientobject.GagManager;
import simple.server.core.event.LoginNotifier;
import simple.server.core.event.TurnNotifier;
import simple.server.core.rule.EntityManager;
import simple.server.core.rule.defaultruleset.DefaultActionManager;
import simple.server.core.rule.defaultruleset.DefaultEntityManager;

public class SimpleSingletonRepository {

    private static EntityManager entityManager = null;
    protected static final HashMap<Class<?>, Object> register = new HashMap<Class<?>, Object>();
    protected static SimpleSingletonRepository instance = null;

    protected SimpleSingletonRepository() {
    }

    protected static void registerSingletons() {
        try {
            register.put(TurnNotifier.class, TurnNotifier.get());
            register.put(LoginNotifier.class, LoginNotifier.get());
            register.put(SimpleRPObjectFactory.class, SimpleRPObjectFactory.get());
            register.put(DefaultActionManager.class, DefaultActionManager.getInstance());
            register.put(SimpleRPRuleProcessor.class, SimpleRPRuleProcessor.get());
            register.put(SimpleRPWorld.class, SimpleRPWorld.get());
            register.put(SimpleDatabase.class, SimpleDatabase.get());
            register.put(GagManager.class, GagManager.get());
        } catch (IOException ex) {
            Logger.getLogger(SimpleSingletonRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SimpleSingletonRepository get() {
        if (instance == null) {
            instance = new SimpleSingletonRepository();
            registerSingletons();
        }
        return instance;
    }

    public static EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = new DefaultEntityManager();
        }
        return entityManager;
    }

    public static TransactionPool getTransactionPool() {
        return TransactionPool.get();
    }

    /**
     * Gets the instance for the requested singleton
     *
     * @param <T>   type of singleton
     * @param clazz class of singleton
     * @return instance of singleton
     * @throws IllegalArgumentException in case there is no instance registered for the specified class
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        T res = (T) register.get(clazz);
        if (res == null) {
            throw new IllegalArgumentException("No singleton registered for class " + clazz);
        }
        return res;
    }

    /**
     * Registers a singleton
     *
     * @param <T>   type of singleton
     * @param clazz class of singleton
     * @param object instance of singleton
     */
    public <T> void register(Class<T> clazz, T object) {
        register.put(clazz, object);
    }
}
