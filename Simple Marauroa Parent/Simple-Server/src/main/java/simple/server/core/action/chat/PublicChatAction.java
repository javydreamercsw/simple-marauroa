package simple.server.core.action.chat;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Configuration;
import marauroa.common.game.Attributes;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.IRPWorld;
import simple.server.core.entity.Entity;
import simple.server.core.event.LoginListener;
import simple.server.core.event.TextEvent;

/**
 * Handles public said text
 */
@ServiceProvider(service = ActionProvider.class)
public class PublicChatAction implements ActionProvider {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(PublicChatAction.class.getSimpleName());
    public static final String CHAT = "chat";

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (Lookup.getDefault().lookup(LoginListener.class).
                    checkIsGaggedAndInformPlayer(player)) {
                return;
            }
            if (action.has(TEXT)) {
                try {
                    String text = action.get(TEXT);
                    LOG.log(Level.FINE, "Processing text event: {0}", text);
                    Lookup.getDefault().lookup(IRPWorld.class).applyPublicEvent(
                            Lookup.getDefault().lookup(IRPWorld.class)
                            .getZone(((Attributes) player)
                                    .get(Entity.ZONE_ID)),
                            new TextEvent(text, player.getName()));
                    if ("true".equals(Configuration.getConfiguration()
                            .get("log_chat", "false"))) {
                        LOG.info(text);
                    }
                } catch (IOException ex) {
                    LOG.log(Level.WARNING, ex.toString(), ex);
                }
            } else {
                StringBuilder mess
                        = new StringBuilder("Action is missing key components:\n");
                if (!action.has(TEXT)) {
                    mess.append(TEXT).append("\n");
                }
                LOG.warning(mess.toString());
            }
        }
    }

    @Override
    public void register() {
        CommandCenter.register(CHAT, new PublicChatAction());
    }
}
