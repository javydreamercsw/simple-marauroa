package simple.client.gui;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import marauroa.common.Log4J;
import marauroa.common.Logger;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import simple.client.RPObjectChangeListener;
import simple.client.entity.ClientEntity;
import simple.client.entity.EntityFactory;

/**
 * Stores the objects that exists on the World right now.
 * 
 */
public class GameObjects implements RPObjectChangeListener, Iterable<ClientEntity> {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(GameObjects.class);
    private final Map<FQID, ClientEntity> objects;
    /**
     * holds the reference to the singleton instance.
     */
    private static GameObjects instance;

    /**
     * @return singleton instance of GameOjects
     */
    public static GameObjects createInstance() {
        if (instance == null) {
            instance = new GameObjects();
        }
        return instance;
    }

    /**
     * @return existing instance of GameObjects
     */
    public static GameObjects getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "GameObject has not been initialized");
        }

        return instance;
    }

    /**
     * constructor.
     *
     */
    private GameObjects() {
        objects = new HashMap<FQID, ClientEntity>();
    }

    @Override
    public Iterator<ClientEntity> iterator() {
        return objects.values().iterator();
    }

    /**
     *
     * @param object
     * @return
     */
    public ClientEntity get(RPObject object) {
        return objects.get(FQID.create(object));
    }

    /**
     *
     * @param id
     * @return
     */
    public ClientEntity get(RPObject.ID id) {
        return objects.get(new FQID(id));
    }

    /**
     * Removes all the object entities.
     */
    public synchronized void clear() {
        if (!objects.isEmpty()) {
            logger.debug("Game objects not empty!");

            // invalidate all entity objects
            Iterator<ClientEntity> it = iterator();

            while (it.hasNext()) {
                ClientEntity entity = it.next();
                logger.error("Residual entity: " + entity);
                entity.release();
            }

            objects.clear();
        }
    }

    /**
     *
     * @param entity
     * @return
     */
    public boolean collides(ClientEntity entity) {
        Rectangle2D area = entity.getArea();

        synchronized (this) {
            for (ClientEntity other : objects.values()) {
                if (other.isObstacle(entity) && area.intersects(other.getArea())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Update objects based on the lapsus of time ellapsed since the last call.
     *
     * @param delta
     *            The time since last update (in ms).
     */
    public synchronized void update(int delta) {
        for (ClientEntity entity : objects.values()) {
            entity.update(delta);
        }
    }

    /**
     * Create an add an ClientEntity. This does not add to the screen list.
     *
     * @param object
     *            The object.
     *
     * @return An entity.
     */
    protected ClientEntity add(final RPObject object) {
        ClientEntity entity = EntityFactory.createEntity(object);

        if (entity != null) {
            synchronized (this) {
                objects.put(FQID.create(object), entity);
            }
        }

        return entity;
    }

    //
    // RPObjectChangeListener
    //
    /**
     * An object was added.
     *
     * @param object
     *            The object.
     */
    @Override
    public void onAdded(final RPObject object) {
        if (object.has("server-only")) {
            logger.debug("Discarding object: " + object);
        } else {
            if (!object.getRPClass().subclassOf("entity")) {
                logger.debug("Skipping non-entity object: " + object);
                return;
            }

            ClientEntity entity = add(object);

            if (entity != null) {
                logger.debug("added " + entity);
            } else {
                logger.error("No entity for: " + object);
            }
        }
    }

    /**
     * The object added/changed attribute(s).
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     */
    @Override
    public void onChangedAdded(final RPObject object, final RPObject changes) {
        ClientEntity entity;

        synchronized (this) {
            entity = objects.get(FQID.create(object));
        }

        if (entity != null) {
            entity.onChangedAdded(object, changes);
        }
    }

    /**
     * An object removed attribute(s).
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     */
    @Override
    public void onChangedRemoved(final RPObject object, final RPObject changes) {
        ClientEntity entity;

        synchronized (this) {
            entity = objects.get(FQID.create(object));
        }

        if (entity != null) {
            entity.onChangedRemoved(object, changes);
        }
    }

    /**
     * An object was removed.
     *
     * @param object
     *            The object.
     */
    @Override
    public void onRemoved(final RPObject object) {
        RPObject.ID id = object.getID();

        logger.debug("removed " + id);

        ClientEntity entity;

        synchronized (this) {
            entity = objects.remove(FQID.create(object));
        }

        if (entity != null) {
            entity.release();
        }
    }

    /**
     * A slot object was added.
     *
     * @param object
     *            The container object.
     * @param slotName
     *            The slot name.
     * @param sobject
     *            The slot object.
     */
    @Override
    public void onSlotAdded(final RPObject object, final String slotName,
            final RPObject sobject) {
    }

    /**
     * A slot object added/changed attribute(s).
     *
     * @param object
     *            The base container object.
     * @param slotName
     *            The container's slot name.
     * @param sobject
     *            The slot object.
     * @param schanges
     *            The slot object changes.
     */
    @Override
    public void onSlotChangedAdded(final RPObject object,
            final String slotName, final RPObject sobject,
            final RPObject schanges) {
        ClientEntity entity;

        synchronized (this) {
            entity = objects.get(FQID.create(object));
        }

        if (entity != null) {
            entity.onSlotChangedAdded(object, slotName, sobject, schanges);
        }
    }

    /**
     * A slot object removed attribute(s).
     *
     * @param object
     *            The base container object.
     * @param slotName
     *            The container's slot name.
     * @param sobject
     *            The slot object.
     * @param schanges
     *            The slot object changes.
     */
    @Override
    public void onSlotChangedRemoved(final RPObject object,
            final String slotName, final RPObject sobject,
            final RPObject schanges) {
        ClientEntity entity;

        synchronized (this) {
            entity = objects.get(FQID.create(object));
        }

        if (entity != null) {
            entity.onSlotChangedRemoved(object, slotName, sobject, schanges);
        }
    }

    /**
     * A slot object was removed.
     *
     * @param object
     *            The container object.
     * @param slotName
     *            The slot name.
     * @param sobject
     *            The slot object.
     */
    @Override
    public void onSlotRemoved(final RPObject object, final String slotName,
            final RPObject sobject) {
    }

    @Override
    public RPObject onRPEvent(RPObject object) {
        return object;
    }

    //
    //
    /**
     * A fully qualified ID. This will make an nested ID unique, even when in a
     * slot tree.
     */
    protected static class FQID {

        /**
         * The object identification path.
         */
        protected Object[] path;

        /**
         * Create a fully qualified ID.
         *
         * @param id
         *            And object ID.
         */
        public FQID(RPObject.ID id) {
            this(new Object[]{Integer.valueOf(id.getObjectID())});
        }

        /**
         * Create a fully qualified ID.
         *
         * @param path
         *            An identification path.
         */
        public FQID(Object[] path) {
            this.path = path;
        }

        //
        // FQID
        //
        /**
         * Create a FQID from an object tree.
         *
         * @param object
         *            An object.
         *
         * @return A FQID.
         */
        public static FQID create(final RPObject object) {
            LinkedList<Object> path = new LinkedList<Object>();
            RPObject node = object;

            while (true) {
                path.addFirst(Integer.valueOf(node.getID().getObjectID()));

                RPSlot slot = node.getContainerSlot();

                if (slot == null) {
                    break;
                }

                path.addFirst(slot.getName());
                node = node.getContainer();
            }

            return new FQID(path.toArray());
        }

        /**
         * Get the tree path of object identifiers.
         *
         * @return The identifier path.
         */
        public Object[] getPath() {
            return path;
        }

        //
        // Object
        //
        /**
         * Check if this equals another object.
         *
         * @param obj
         *            The object to compare to.
         */
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof FQID)) {
                return false;
            }

            return Arrays.equals(getPath(), ((FQID) obj).getPath());
        }

        /**
         * Get the hash code.
         *
         * @return The hash code.
         */
        @Override
        public int hashCode() {
            int value = 0;

            for (Object obj : getPath()) {
                value ^= obj.hashCode();
            }

            return value;
        }

        /**
         * Get the string representation.
         *
         * @return The string representation.
         */
        @Override
        public String toString() {
            StringBuilder sbuf = new StringBuilder();

            sbuf.append('[');
            sbuf.append(path[0]);

            for (int i = 1; i < path.length; i++) {
                sbuf.append(':');
                sbuf.append(path[i]);
            }

            sbuf.append(']');

            return sbuf.toString();
        }
    }
}
