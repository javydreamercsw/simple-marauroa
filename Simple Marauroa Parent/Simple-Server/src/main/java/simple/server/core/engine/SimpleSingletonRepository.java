package simple.server.core.engine;

import marauroa.server.db.TransactionPool;
import simple.server.core.rule.EntityManager;
import simple.server.core.rule.defaultruleset.DefaultEntityManager;

public class SimpleSingletonRepository {

    private static EntityManager entityManager = null;

    private SimpleSingletonRepository() {
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
}
