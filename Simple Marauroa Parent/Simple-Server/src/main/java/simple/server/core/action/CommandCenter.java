package simple.server.core.action;

import java.util.concurrent.ConcurrentHashMap;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.admin.AdministrationAction;

public class CommandCenter {

    //TODO: Replace with Lookup
    private static final UnknownAction UNKNOWN_ACTION = new UnknownAction();
    private static volatile ConcurrentHashMap<String, ActionInterface> actionsMap;
    private static final Logger logger = Log4J.getLogger(CommandCenter.class);

    protected static ConcurrentHashMap<String, ActionInterface> getActionsMap() {
        if (actionsMap == null) {
            actionsMap = new ConcurrentHashMap<String, ActionInterface>();
        }
        return actionsMap;
    }

    public static void register(String action, ActionInterface actionClass) {
        getActionsMap().putIfAbsent(action, actionClass);
    }

    public static void registerAndOverwrite(String action, ActionInterface actionClass) {
        getActionsMap().put(action, actionClass);
    }

    public static void registerAndOverwrite(String action, ActionInterface actionClass,
            int requiredAdminLevel) {
        registerAndOverwrite(action, actionClass);
        AdministrationAction.registerCommandLevel(action, requiredAdminLevel);
    }

    public static void register(String action, ActionInterface actionClass,
            int requiredAdminLevel) {
        register(action, actionClass);
        AdministrationAction.registerCommandLevel(action, requiredAdminLevel);
    }

    public static boolean execute(ClientObjectInterface player, RPAction action) {
        try {
            ActionInterface actionInterface = getAction(action);
            actionInterface.onAction((RPObject) player, action);
            return true;
        } catch (Exception e) {
            logger.error("Cannot execute action " + action + " send by " + player, e);
            return false;
        }
    }

    private static ActionInterface getAction(RPAction action) {
        if (action == null) {
            return UNKNOWN_ACTION;
        } else {
            return getAction(action.get("type"));
        }
    }

    private static ActionInterface getAction(String type) {
        if (type == null) {
            return UNKNOWN_ACTION;
        }

        ActionInterface action = getActionsMap().get(type);
        if (action == null) {
            return UNKNOWN_ACTION;
        } else {
            return action;
        }
    }

    static class UnknownAction implements ActionInterface {

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
                player.sendPrivateText("Unknown command '" + type + "'");
            }
        }
    }

    public CommandCenter() {
    }
}
