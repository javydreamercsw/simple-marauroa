package simple.client.entity;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import simple.client.SimpleClient;

/**
 * creates a ClientEntity object based on a Marauroa RPObject.
 *
 * @author astridemma
 */
public class EntityFactory {

    private static final Logger LOG
            = Logger.getLogger(EntityFactory.class.getSimpleName());

    /**
     *
     */
    protected EntityFactory() {
    }

    /**
     * Creates an ClientEntity of the correct type depending on the Marauroa
     * object.
     *
     * @param object the underlying server RPObject
     * @return the created ClientEntity
     */
    public static ClientEntity createEntity(final RPObject object) {
        try {
            if (object.has("type")) {
                String type = object.get("type");

                if (type.equals("player") && object.has("name")) {
                    if (SimpleClient.get().getAccountUsername().equalsIgnoreCase(
                            object.get("name"))) {
                        User me = new User();
                        me.initialize(object);
                        return me;
                    }
                }

                String eclass = null;
                if (object.has("class")) {
                    eclass = object.get("class");
                }

                Class<? extends ClientEntity> entityClass
                        = EntityMap.getClass(type, eclass);
                if (entityClass == null) {
                    // If there is no entity, let's try without using class.
                    entityClass = EntityMap.getClass(type, null);

                    if (entityClass == null) {
                        return null;
                    }
                }

                ClientEntity en = entityClass.newInstance();
                en.initialize(object);
                return en;
            } else {
                ClientEntity en = new ClientEntity();
                en.initialize(object);
                return en;
            }

        } catch (IllegalAccessException | InstantiationException e) {
            LOG.log(Level.SEVERE,
                    "Error creating entity for object: " + object, e);
            return null;
        }
    }
}
