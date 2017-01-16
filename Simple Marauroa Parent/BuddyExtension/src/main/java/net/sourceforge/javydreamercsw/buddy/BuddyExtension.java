package net.sourceforge.javydreamercsw.buddy;

import marauroa.common.game.Attributes;
import marauroa.common.game.Definition;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.common.SimpleException;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import simple.server.core.action.WellKnownActionConstant;
import static simple.server.core.action.WellKnownActionConstant.TARGET;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.extension.MarauroaServerExtension;
import simple.server.extension.SimpleServerExtension;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProviders({
    @ServiceProvider(service = MarauroaServerExtension.class)
    ,@ServiceProvider(service = ActionProvider.class)})
public class BuddyExtension extends SimpleServerExtension implements
        ActionProvider {

    public static final String GRUMPY = "grumpy", BUDDY = "!buddy";
    public static final String UNIGNORE = "unignore";
    public static final String REMOVEBUDDY = "removebuddy";
    public static final String IGNORE = "ignore";
    public static final String ADDBUDDY = "addbuddy";
    public static final String BUDDY_ONLINE = "1";
    public static final String BUDDY_OFFLINE = "0";
    public static final String REASON = "reason";
    public static final String DURATION = "duration";

    @Override
    public void modifyClientObjectDefinition(RPClass client) {
        // We use this for the buddy system
        client.addRPSlot(BUDDY, 1, Definition.PRIVATE);
        client.addRPSlot(IGNORE, 1, Definition.HIDDEN);

        client.addAttribute(BUDDY_ONLINE, Definition.Type.LONG_STRING,
                (byte) (Definition.PRIVATE | Definition.VOLATILE));
        client.addAttribute(BUDDY_OFFLINE, Definition.Type.LONG_STRING,
                (byte) (Definition.PRIVATE | Definition.VOLATILE));

    }

    @Override
    public void clientObjectUpdate(ClientObjectInterface client)
            throws SimpleException {
        if (!((Attributes) client).has(BUDDY_ONLINE)) {
            ((Attributes) client).put(BUDDY_ONLINE, "");
        }
        if (!((Attributes) client).has(BUDDY_OFFLINE)) {
            ((Attributes) client).put(BUDDY_OFFLINE, "");
        }
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (action.get(WellKnownActionConstant.TYPE).equals(ADDBUDDY)) {
                String who = action.get(TARGET);
                String online = BUDDY_OFFLINE;
                ClientObject buddy = new ClientObject((RPObject) ((SimpleRPRuleProcessor) Lookup.getDefault()
                        .lookup(IRPRuleProcessor.class)).getPlayer(who));
                if (!buddy.isGhost()) {
                    online = BUDDY_ONLINE;
                }
                player.setKeyedSlot(BUDDY, "_" + who, online);

                ((SimpleRPRuleProcessor) Lookup.getDefault()
                        .lookup(IRPRuleProcessor.class))
                        .addGameEvent(player.getName(), "buddy",
                                "add", who);
            }
            if (action.get(WellKnownActionConstant.TYPE).equals(UNIGNORE)) {
                if (action.has(TARGET)) {
                    String who = action.get(TARGET);

                    if (player.setKeyedSlot("!ignore", "_" + who, null)) {
                        player.sendPrivateText(who
                                + " was removed from your ignore list.");
                    }
                }
            }
            if (action.get(WellKnownActionConstant.TYPE).equals(REMOVEBUDDY)) {
                if (action.has(TARGET)) {
                    String who = action.get(TARGET);

                    player.setKeyedSlot(BUDDY, "_" + who, null);

                    ((SimpleRPRuleProcessor) Lookup.getDefault()
                            .lookup(IRPRuleProcessor.class))
                            .addGameEvent(player.getName(),
                                    "buddy", "remove", who);

                    player.setKeyedSlot("!ignore", "_" + who, null);
                }
            }
            if (action.get(WellKnownActionConstant.TYPE).equals(IGNORE)) {
                int duration;
                String reason;

                if (action.has(TARGET)) {
                    String who = action.get(TARGET);

                    if (action.has(DURATION)) {
                        duration = action.getInt(DURATION);
                    } else {
                        duration = 0;
                    }

                    if (action.has(REASON)) {
                        reason = action.get(REASON);
                    } else {
                        reason = null;
                    }

                    if (addIgnore(player, who, duration, reason)) {
                        player.sendPrivateText(who
                                + " was added to your ignore list.");
                    }
                }
            }
            if (action.get(WellKnownActionConstant.TYPE).equals(GRUMPY)) {
                if (action.has(REASON)) {
                    setGrumpyMessage(player, action.get(REASON));
                } else {
                    setGrumpyMessage(player, null);
                }
                player.notifyWorldAboutChanges();
            }
        }
    }

    /**
     * Add a player ignore entry.
     *
     * @param coi Player
     * @param name The player name.
     * @param duration The ignore duration (in minutes), or <code>0</code> for
     * infinite.
     * @param reply The reply.
     *
     * @return <code>true</code> if value changed, <code>false</code> if there
     * was a problem.
     */
    public boolean addIgnore(ClientObjectInterface coi, String name,
            int duration, String reply) {
        StringBuilder sbuf = new StringBuilder();

        if (duration != 0) {
            sbuf.append(System.currentTimeMillis() + (duration * 60000L));
        }

        sbuf.append(';');

        if (reply != null) {
            sbuf.append(reply);
        }

        return coi.setKeyedSlot(IGNORE, "_" + name, sbuf.toString());
    }

    /**
     * Notifies this player that the given player has logged out.
     *
     * @param coi Player
     * @param who The name of the player who has logged out.
     */
    public void notifyOffline(ClientObjectInterface coi, String who) {
        String playerOffline = "_" + who;

        boolean found = false;
        RPSlot slot = coi.getSlot(BUDDY);
        if (slot != null && slot.size() > 0) {
            RPObject buddies = slot.iterator().next();
            for (String name : buddies) {
                if (playerOffline.equals(name)) {
                    buddies.put(playerOffline, 0);
                    coi.notifyWorldAboutChanges();
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            if (((RPObject) coi).has("offline")) {
                ((RPObject) coi).put("offline",
                        ((RPObject) coi).get("offline") + "," + who);
            } else {
                ((RPObject) coi).put("offline", who);
            }
        }
    }

    @Override
    public String getName() {
        return "Buddy Extension";
    }

    @Override
    public void register() {
        CommandCenter.register(ADDBUDDY, BuddyExtension.this);
        CommandCenter.register(IGNORE, BuddyExtension.this);
        CommandCenter.register(REMOVEBUDDY, BuddyExtension.this);
        CommandCenter.register(UNIGNORE, BuddyExtension.this);
        CommandCenter.register(GRUMPY, BuddyExtension.this);
    }

    private void setGrumpyMessage(ClientObjectInterface player, String get) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
