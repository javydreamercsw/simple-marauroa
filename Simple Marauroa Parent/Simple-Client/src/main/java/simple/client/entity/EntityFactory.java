
package simple.client.entity;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import simple.client.SimpleClient;

/**
 * creates a ClientEntity object based on a Marauroa RPObject.
 * 
 * @author astridemma
 */
public class EntityFactory {

    private static final Logger logger = Log4J.getLogger(EntityFactory.class);

    /**
     *
     */
    protected EntityFactory() {
    }

    /**
     * Creates an ClientEntity of the correct type depending on the Marauroa object.
     *
     * @param object
     *            the underlying server RPObject
     * @return the created ClientEntity
     */
    public static ClientEntity createEntity(final RPObject object) {
        try {
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

            Class<? extends ClientEntity> entityClass = EntityMap.getClass(type, eclass);
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
        } catch (Exception e) {
            logger.error("Error creating entity for object: " + object, e);
            return null;
        }
    }
}
