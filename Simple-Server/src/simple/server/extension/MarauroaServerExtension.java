package simple.server.extension;

import marauroa.common.game.*;
import simple.common.game.ClientObjectInterface;

public interface MarauroaServerExtension {

    /**
     * Action to perform
     *
     * @param player Player requesting the action
     * @param action Action details
     */
    public abstract void onAction(RPObject player, RPAction action);

    /**
     * Query the extension to plug in any action when an object is added to a
     * zone.
     *
     * @param object Added object
     * @return potentially modified object
     */
    public RPObject onRPObjectAddToZone(RPObject object);

    /**
     * Query the extension to plug in any action when an object is removed from
     * a zone.
     *
     * @param object Removed object
     * @return potentially modified object
     */
    public RPObject onRPObjectRemoveFromZone(RPObject object);

    /**
     * Query the extension to plug in any changes to the perception of an
     * object.
     *
     * @param object Object to potentially modify the perception
     * @param type
     */
    public void getPerception(RPObject object, byte type, Perception p);

    /**
     * Plug into the definition of the client class
     *
     * @param client
     */
    public void modifyClientObjectDefinition(RPClass client);

    /**
     * Plug into the definition of the root RPClass. The root class is
     * considered the base class from all others inherit attributes from. Use
     * this for attributes that will be common among all entities in the
     * application. Use modifyClientObjectDefinition for attributes only to be
     * common among client objects.
     *
     * @param root
     */
    public void modifyRootRPClassDefinition(RPClass root);

    /**
     * Action to perform after the world is initialized (all classes are
     * defined)
     */
    public void afterWorldInit();

    /**
     * Update the database. Register/Update DAO's here as well
     */
    public void updateDatabase();

    /**
     * Query the extension to plug in any changes to the perception of an
     * object. This is called after the normal perceptions are sent.
     *
     * @param object Object to potentially modify the perception
     */
    public boolean updateMonitor(RPObject object, Perception perception);

    /**
     * When zone is added
     *
     * @param zone Zone added
     */
    public void onAddRPZone(IRPZone zone);

    /**
     * When zone is removed
     *
     * @param zone Zone removed
     */
    public void onRemoveRPZone(IRPZone zone);

    /**
     * Client object update. This initializes attributes on the object. Useful
     * when adding new attributes to existing objects so they get populated with
     * valid initial values.
     */
    public void clientObjectUpdate(ClientObjectInterface client);

    /**
     * Root RPClass object update. This initializes attributes on the object.
     * Useful when adding new attributes to existing objects so they get
     * populated with valid initial values.
     */
    public void rootRPClassUpdate(RPObject entity);
}