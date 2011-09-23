/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.event;

import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;

/**
 * Delays the sending of text (until the next turn for instance to work
 * around problems like zone changes).
 */
class DelayedPlayerTextSender implements TurnListener {

    private ClientObjectInterface player;
    private String message;
    private NotificationType type;

    /**
     * Creates a new private message type DelayedPlayerTextSender.
     *
     * @param player
     *            ClientObjectInterface to send this message to
     * @param message
     *            message
     */
    DelayedPlayerTextSender(ClientObjectInterface player, String message) {
        this.player = player;
        this.message = message;
        this.type = NotificationType.PRIVMSG;
    }

    /**
     * Creates a new DelayedPlayerTextSender.
     *
     * @param player
     *            ClientObjectInterface to send this message to
     * @param message
     *            message
     * @param type
     *            logical notificationType
     */
    DelayedPlayerTextSender(ClientObjectInterface player, String message, NotificationType type) {
        this.player = player;
        this.message = message;
        this.type = type;
    }

    @Override
    public void onTurnReached(int currentTurn) {
        player.sendPrivateText(type, message);
    }
}
