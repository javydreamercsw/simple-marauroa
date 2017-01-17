package simple.client.entity;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.client.event.listener.ClientRPEventListener;
import simple.client.event.listener.ClientRPEventNotifier;
import simple.client.gui.IGameObjects;

/**
 * The player user context. This class holds/manages the data for the user of
 * this client. This is independent of any on-screen representation ClientEntity
 * that, while related, serves an entirely different purpose.
 *
 * Currently this is just a helper class for jWrestlingClient. Maybe it will be
 * directly used by other code later.
 */
@ServiceProvider(service = IUserContext.class)
public class UserContext implements IUserContext {

    /**
     * The logger.
     */
    private static final Logger LOG
            = Logger.getLogger(UserContext.class.getSimpleName());
    /**
     * The currently known buddies.
     */
    protected HashMap<String, Boolean> buddies;
    /**
     * The currently enabled features.
     */
    protected HashMap<String, String> features;
    /**
     * The RPEvent listeners.
     */
    protected ClientRPEventNotifier eventNotifier;
    /**
     * The administrator level.
     */
    protected int adminlevel;
    /**
     * The player character's name.
     */
    protected String name;

    /**
     * Constructor.
     *
     */
    public UserContext() {
        adminlevel = 0;
        eventNotifier = ClientRPEventNotifier.get();
        name = null;
        buddies = new HashMap<>();
        features = new HashMap<>();
    }

    /**
     * Register an RPEvent listener
     *
     * @param event event to listen for
     * @param listener listener
     */
    @Override
    public void registerClientRPEventListener(Class<? extends RPEvent> event,
            ClientRPEventListener listener) {
        LOG.log(Level.FINE, "Adding event: {0} to the listener list with "
                + "listener: {1}", new Object[]{event.getName(),
                    listener.getClass().getSimpleName()});
        eventNotifier.notifyAtEvent(event, listener);
    }

    /**
     * Fire administrative level change event to all registered listeners.
     *
     * @param adminLevel The new administrative level.
     */
    protected void fireAdminLevelChanged(int adminLevel) {
        // TODO: Impl
    }

    /**
     * Fire name change event to all registered listeners.
     *
     * @param newName The new player name.
     */
    protected void fireNameChanged(String newName) {
        // TODO: Impl
    }

    /**
     * Get the administrator level.
     *
     * @return The administrator level.
     */
    @Override
    public int getAdminLevel() {
        return adminlevel;
    }

    /**
     * Get the player character name.
     *
     * @return The player character name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Determine if the user is an admin.
     *
     * @return <code>true</code> is the user is an admin.
     */
    @Override
    public boolean isAdmin() {
        return (getAdminLevel() != 0);
    }

    //
    // RPObjectChangeListener
    //
    /**
     * An object was added.
     *
     * @param object The object.
     */
    @Override
    public void onAdded(final RPObject object) {
        if (object.has("adminlevel")) {
            adminlevel = object.getInt("adminlevel");
            fireAdminLevelChanged(adminlevel);
        }
    }

    /**
     * The object added/changed attribute(s).
     *
     * @param object The base object.
     * @param changes The changes.
     */
    @Override
    public void onChangedAdded(final RPObject object, final RPObject changes) {
        if (changes.has("adminlevel")) {
            adminlevel = changes.getInt("adminlevel");
            fireAdminLevelChanged(adminlevel);
        }

        if (changes.has("name")) {
            name = changes.get("name");
            fireNameChanged(name);
        }
    }

    /**
     * An object removed attribute(s).
     *
     * @param object The base object.
     * @param changes The changes.
     */
    @Override
    public void onChangedRemoved(final RPObject object, final RPObject changes) {
        if (changes.has("adminlevel")) {
            adminlevel = 0;
            fireAdminLevelChanged(adminlevel);
        }

        if (changes.has("name")) {
            name = null;
            fireNameChanged(name);
        }
    }

    /**
     * An object was removed.
     *
     * @param object The object.
     */
    @Override
    public void onRemoved(final RPObject object) {
        adminlevel = 0;
        fireAdminLevelChanged(adminlevel);

        name = null;
        fireNameChanged(null);
    }

    /**
     * A slot object was added.
     *
     * @param object The container object.
     * @param slotName The slot name.
     * @param sobject The slot object.
     */
    @Override
    public void onSlotAdded(final RPObject object, final String slotName,
            final RPObject sobject) {
        if (sobject.getRPClass().subclassOf("entity")) {
            synchronized (Lookup.getDefault().lookup(IGameObjects.class)) {
                ClientEntity entity = Lookup.getDefault().lookup(IGameObjects.class).get(sobject);

                if (entity != null) {
                    ClientEntity parent = Lookup.getDefault().lookup(IGameObjects.class).get(object);

                    LOG.log(Level.FINE, "Added: {0}", entity);
                    LOG.log(Level.FINE, "   To: {0}  [{1}]",
                            new Object[]{parent, slotName});
                }
            }
        }
    }

    /**
     * A slot object added/changed attribute(s).
     *
     * @param object The base container object.
     * @param slotName The container's slot name.
     * @param sobject The slot object.
     * @param schanges The slot object changes.
     */
    @Override
    public void onSlotChangedAdded(final RPObject object,
            final String slotName, final RPObject sobject,
            final RPObject schanges) {
    }

    /**
     * A slot object removed attribute(s).
     *
     * @param object The base container object.
     * @param slotName The container's slot name.
     * @param sobject The slot object.
     * @param schanges The slot object changes.
     */
    @Override
    public void onSlotChangedRemoved(final RPObject object,
            final String slotName, final RPObject sobject,
            final RPObject schanges) {
    }

    /**
     * A slot object was removed.
     *
     * @param object The container object.
     * @param slotName The slot name.
     * @param sobject The slot object.
     */
    @Override
    public void onSlotRemoved(final RPObject object, final String slotName,
            final RPObject sobject) {
        if (sobject.getRPClass().subclassOf("entity")) {
            synchronized (Lookup.getDefault().lookup(IGameObjects.class)) {
                ClientEntity entity
                        = Lookup.getDefault().lookup(IGameObjects.class).get(sobject);

                if (entity != null) {
                    ClientEntity parent
                            = Lookup.getDefault().lookup(IGameObjects.class).get(object);

                    LOG.log(Level.FINE, "Removed: {0}", entity);
                    LOG.log(Level.FINE, "   From: {0}  [{1}]",
                            new Object[]{parent, slotName});
                }
            }
        }
    }

    /**
     * Be aware that this gets rid of all events after its done!
     *
     * @param object Object to process events from
     * @return Modified object
     */
    @Override
    public RPObject onRPEvent(RPObject object) {
        HashMap<RPEvent, Boolean> result = eventNotifier.logic(object.events());
        if (!result.entrySet().isEmpty()) {
            LOG.fine("Here are the processed events. A false means "
                    + "that probably RPEventListeners not registered.\n");
            result.entrySet().forEach((e) -> {
                LOG.log(Level.FINE, "{0} Processed? {1}",
                        new Object[]{e.getKey(), e.getValue()});
            });
        } else if (!object.events().isEmpty()) {
            LOG.warning("Unable to process events:");
            object.events().forEach((e) -> {
                LOG.info(e.toString());
            });
        }
        return object;
    }
}
