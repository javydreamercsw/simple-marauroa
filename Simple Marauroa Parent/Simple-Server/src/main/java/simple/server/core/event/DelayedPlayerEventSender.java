package simple.server.core.event;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPZone;

/**
 * Delays the sending of event (until the next turn for instance to work around
 * problems like zone changes).
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DelayedPlayerEventSender implements TurnListener {

    private RPEvent event;
    private RPObject player;
    private SimpleRPZone zone;

    public DelayedPlayerEventSender(RPEvent e, RPObject p) {
        this.event = e;
        this.player = p;
    }

    public DelayedPlayerEventSender(RPEvent e, SimpleRPZone z) {
        this.event = e;
        this.zone = z;
    }

    @Override
    public void onTurnReached(int currentTurn) {
        //If player is not null send the event to that player only
        if (player != null) {
            player.addEvent(event);
            if (player instanceof ClientObjectInterface) {
                ((ClientObjectInterface) player).notifyWorldAboutChanges();
            } else {
                Lookup.getDefault().lookup(IRPWorld.class).modify(player);
            }
        } /**
         * Other wise set it up to send as a public event. If zone is null it's
         * sent to everyone. If zone is not null it's sent to everyone on that
         * zone.
         */
        else {
            Lookup.getDefault().lookup(IRPWorld.class).applyPublicEvent(zone, event);
        }
    }
}
