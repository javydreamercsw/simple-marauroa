package simple.server.core.engine;

import marauroa.common.game.RPObject;
import simple.common.game.ClientObjectInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IRPObjectFactory {

    /**
     * Create client object from RPObject
     *
     * @param object RPObject to create from
     * @return Client Object
     */
    ClientObjectInterface createClientObject(RPObject object);

    /**
     * Create default client object with provided name
     *
     * @param name Name for the new object
     * @return Client Object
     */
    ClientObjectInterface createDefaultClientObject(String name);

    /**
     * Create default client object from provided RPObject
     *
     * @param entity RPObject to create it from
     * @return Client Object
     */
    ClientObjectInterface createDefaultClientObject(RPObject entity);

    /**
     * Destroy the provided client object
     *
     * @param object Client Object to destroy
     */
    void destroyClientObject(ClientObjectInterface object);

    /**
     * Transform RPObject
     *
     * @param object RPObject to transform
     * @return transformed RPObject
     */
    RPObject transform(RPObject object);
}
