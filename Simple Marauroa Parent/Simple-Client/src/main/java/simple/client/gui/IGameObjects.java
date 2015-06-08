/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package simple.client.gui;

import marauroa.common.game.RPObject;
import simple.client.RPObjectChangeListener;
import simple.client.entity.ClientEntity;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IGameObjects extends Iterable<ClientEntity>, RPObjectChangeListener {
    /**
     * Get object from world
     * @param object Object to retrieve
     * @return retrieved object
     */
    public ClientEntity get(RPObject object);
    
    /**
     * Update objects based on the lapsus of time elapsed since the last call.
     *
     * @param delta The time since last update (in ms).
     */
    public void update(int delta);
}
