package simple.server.core.action.chat;

import java.io.IOException;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import static simple.server.core.action.WellKnownActionConstant.TARGET;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.IRPWorld;
import simple.server.core.event.LoginListener;
import simple.server.core.event.PrivateTextEvent;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = ActionProvider.class)
public class PrivateChatAction implements ActionProvider {

    /**
     * the logger instance.
     */
    private static final Logger LOG = Log4J.getLogger(PrivateChatAction.class);
    public static final String PRIVATE_CHAT = PrivateTextEvent.RPCLASS_NAME;

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            if (Lookup.getDefault().lookup(LoginListener.class)
                    .checkIsGaggedAndInformPlayer(player)) {
                return;
            }
            if (action.has(TEXT) && action.has(TARGET)) {
                try {
                    String text = action.get(TEXT);
                    String target = action.get(TARGET);
                    String from = rpo.get("name");
                    LOG.info("Processing private text action: " + action);
                    Lookup.getDefault().lookup(IRPWorld.class).applyPrivateEvent(target,
                            new PrivateTextEvent(NotificationType.PRIVMSG, text,
                                    target, from));
                    if ("true".equals(Configuration.getConfiguration()
                            .get("log_chat", "false"))) {
                        LOG.info(text);
                    }
                } catch (IOException ex) {
                    LOG.warn(ex.toString(), ex);
                }
            } else {
                StringBuilder mess
                        = new StringBuilder("Action is missing key components:\n");
                if (!action.has(TEXT)) {
                    mess.append(TEXT).append("\n");
                }
                if (!action.has(TARGET)) {
                    mess.append(TARGET).append("\n");
                }
                LOG.warn(mess.toString());
            }
        }
    }

    @Override
    public void register() {
        CommandCenter.register(PRIVATE_CHAT, new PrivateChatAction());
    }
}
