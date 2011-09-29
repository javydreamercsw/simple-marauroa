package simple.server.core.event;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.engine.SimpleSingletonRepository;

/**
 * Delays the sending of event (until the next turn for instance to work
 * around problems like zone changes).
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DelayedPlayerEventSender implements TurnListener {

    private RPEvent event;
    private ClientObjectInterface player;
    private SimpleRPZone zone;

    public DelayedPlayerEventSender(RPEvent e, ClientObjectInterface p) {
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
            ((RPObject)player).addEvent(event);
            player.notifyWorldAboutChanges();
        } /**
         * Other wise set it up to send as a public event.
         * If zone is null it's sent to everyone.
         * If zone is not null it's sent to everyone on that zone.
         */
        else {
            SimpleSingletonRepository.get().get(SimpleRPWorld.class)
                    .applyPublicEvent(zone, event);
        }
    }
}
