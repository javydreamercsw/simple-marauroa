/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.action.chat;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionListener;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.engine.SimpleSingletonRepository;
import simple.server.core.entity.clientobject.GagManager;
import simple.server.core.event.TextEvent;

/**
 * Handles public said text
 */
public class PublicChatAction implements ActionListener {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(PublicChatAction.class);

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (GagManager.checkIsGaggedAndInformPlayer(player)) {
                return;
            }
            if (action.has(TEXT)) {
                String text = action.get(TEXT);
                logger.debug("Processing text event: " + text);
                SimpleSingletonRepository.get().get(SimpleRPWorld.class).applyPublicEvent(
                        SimpleSingletonRepository.get().get(SimpleRPWorld.class).getZone(((RPObject) player).get("zoneid")),
                        new TextEvent(text, player.getName()));
            }
        }
    }
}
