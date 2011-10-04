/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package simple.server.core.engine;

import marauroa.common.game.RPObject;
import simple.common.game.ClientObjectInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IRPObjectFactory {

    ClientObjectInterface createClientObject(RPObject object);

    ClientObjectInterface createDefaultClientObject(String name);

    ClientObjectInterface createDefaultClientObject(RPObject entity);

    void destroyClientObject(ClientObjectInterface object);

    RPObject transform(RPObject object);
}
