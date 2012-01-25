package simple.server.core.action;


import java.util.concurrent.ConcurrentHashMap;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.admin.AdministrationAction;
import simple.server.core.action.chat.ChatAction;

public class CommandCenter {

    private static final UnknownAction UNKNOWN_ACTION = new UnknownAction();
    private static ConcurrentHashMap<String, ActionListener> actionsMap;
    private static final Logger logger = Log4J.getLogger(CommandCenter.class);

    protected static ConcurrentHashMap<String, ActionListener> getActionsMap() {
        if (actionsMap == null) {
            actionsMap = new ConcurrentHashMap<String, ActionListener>();
            registerActions();
        }
        return actionsMap;
    }

    public static void register(String action, ActionListener actionClass) {
        getActionsMap().putIfAbsent(action, actionClass);
    }

    public static void registerAndOverwrite(String action, ActionListener actionClass) {
        getActionsMap().put(action, actionClass);
    }

    public static void registerAndOverwrite(String action, ActionListener actionClass,
            int requiredAdminLevel) {
        registerAndOverwrite(action, actionClass);
        AdministrationAction.registerCommandLevel(action, requiredAdminLevel);
    }

    public static void register(String action, ActionListener actionClass,
            int requiredAdminLevel) {
        register(action, actionClass);
        AdministrationAction.registerCommandLevel(action, requiredAdminLevel);
    }

    protected static void registerActions() {
        AdministrationAction.register();
        ChatAction.register();
    }

    public static boolean execute(ClientObjectInterface player, RPAction action) {
        try {
            ActionListener actionListener = getAction(action);
            actionListener.onAction((RPObject) player, action);
            return true;
        } catch (Exception e) {
            logger.error("Cannot execute action " + action + " send by " + player, e);
            return false;
        }
    }

    private static ActionListener getAction(RPAction action) {
        if (action == null) {
            return UNKNOWN_ACTION;
        } else {
            return getAction(action.get("type"));
        }
    }

    private static ActionListener getAction(String type) {
        if (type == null) {
            return UNKNOWN_ACTION;
        }

        ActionListener action = getActionsMap().get(type);
        if (action == null) {
            return UNKNOWN_ACTION;
        } else {
            return action;
        }
    }

    static class UnknownAction implements ActionListener {

        private static final Logger logger = Log4J.getLogger(UnknownAction.class);

        @Override
        public void onAction(RPObject rpo, RPAction action) {
            if (rpo instanceof ClientObjectInterface) {
                ClientObjectInterface player = (ClientObjectInterface) rpo;
                String type = "null";
                if (action != null) {
                    type = action.get("type");
                }
                logger.warn(player + " tried to execute unknown action " + type);
                if (player != null) {
                    player.sendPrivateText("Unknown command '" + type + "'");
                }
            }
        }
    }
}
