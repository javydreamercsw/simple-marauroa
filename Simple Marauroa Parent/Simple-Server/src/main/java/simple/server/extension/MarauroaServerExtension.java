package simple.server.extension;

import marauroa.common.game.*;
import simple.common.SimpleException;
import simple.common.game.ClientObjectInterface;

public interface MarauroaServerExtension {

    /**
     * Get the plug-in's name
     *
     * @return plug-in's name
     */
    public abstract String getName();

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
     * @param p Perception
     */
    public void getPerception(RPObject object, byte type, Perception p);

    /**
     * Plug into the definition of the client class.
     *
     * @param client
     */
    public void modifyClientObjectDefinition(RPClass client);

    /**
     * Plug into the definition of the root RPClass. The root class is
     * considered the base class from all others inherit attributes from. Use
     * this for attributes that will be common among all entities in the
     * application.
     *
     * @param root root class to modify.
     */
    public void modifyRootRPClassDefinition(RPClass root);

    /**
     * Plug into the definition of the item RPClass. The item class is as
     * special type of entity meant to be consumed. Use this for attributes that
     * will be common among all items in the application.
     *
     * @param item Item to modify.
     */
    public void modifyItemRPClassDefinition(RPClass item);

    /**
     * Plug into the definition of the character RPClass. The character class is
     * as special type of entity meant to be for both players and NPC's. Use
     * this for attributes that will be common among all characters in the
     * application.
     *
     * @param character Character to modify.
     */
    public void modifyCharacterRPClassDefinition(RPClass character);

    /**
     * Plug into the definition of the root Entity RPClass. The root class is
     * considered the base class from all others inherit attributes from. Use
     * this for attributes that will be common among all entities in the
     * application. Use modifyClientObjectDefinition for attributes only to be
     * common among client objects.
     *
     * @param root
     */
    public void modifyRootEntityRPClassDefinition(RPClass root);

    /**
     * Action to perform after the world is initialized (all classes are
     * defined).
     */
    public void afterWorldInit();

    /**
     * Update the database. Register/Update DAO's here as well
     */
    public void updateDatabase();

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
     *
     * @param client Client object
     * @throws SimpleException on any error
     */
    public void clientObjectUpdate(ClientObjectInterface client)
            throws SimpleException;

    /**
     * Root RPClass object update. This initializes attributes on the object.
     * Useful when adding new attributes to existing objects so they get
     * populated with valid initial values.
     *
     * @param entity
     */
    public void rootRPClassUpdate(RPObject entity);

    /**
     * Entity RPClass object update. This initializes attributes on the
     * RPClasses extending Entity. Useful when adding new attributes to existing
     * objects so they get populated with valid initial values.
     *
     * @param entity
     */
    public void entityRPClassUpdate(RPObject entity);

    /**
     * Item RPClass object update. This initializes attributes on the RPClasses
     * extending Entity. Useful when adding new attributes to existing objects
     * so they get populated with valid initial values.
     *
     * @param entity
     */
    public void itemRPClassUpdate(RPObject entity);

    /**
     * Character RPClass object update. This initializes attributes on the
     * RPClasses extending Entity. Useful when adding new attributes to existing
     * objects so they get populated with valid initial values.
     *
     * @param entity
     */
    public void characterRPClassUpdate(RPObject entity);

    /**
     * Do something when an attribute is added to a RPClass
     *
     * @param rpclass RPClass being modified
     * @param name Name of attribute
     * @param type Type of attribute
     * @param flags Flags of attribute
     */
    public void onRPClassAddAttribute(RPClass rpclass,
            String name, Definition.Type type, byte flags);

    /**
     * Do something when an attribute is added to a RPClass
     *
     * @param rpclass RPClass being modified
     * @param name Name of attribute
     * @param type Type of attribute
     */
    public void onRPClassAddAttribute(RPClass rpclass,
            String name, Definition.Type type);
}
