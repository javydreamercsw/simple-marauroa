package simple.client.action;

import java.util.HashMap;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ActionRepository {

    /**
     * Set of client supported Actions.
     */
    private static final HashMap<String, SlashAction> ACTIONS
            = new HashMap<String, SlashAction>();

    /**
     * Registers the available Action.
     *
     * @param command Unique command for this action
     * @param action Action class to handle the command
     */
    public static void register(String command, SlashAction action) {
        ACTIONS.put(command, action);
    }

    /**
     * Gets the Action object for the specified Action name.
     *
     * @param name name of Action
     * @return Action object
     */
    public static SlashAction get(String name) {
        return ACTIONS.get(name);
    }

    /**
     * Gets the help messages of all registered Actions.
     *
     * @param detailed Additional details of the command usage
     */
    public static void getHelpMessages(boolean detailed) {
        ACTIONS.values().forEach((action) -> {
            action.usage(detailed);
        });
    }

    /**
     * Gets the help messages for the action of the matching Actions.
     *
     * @param command Name of Action
     * @param detailed Additional details of the command usage
     */
    public static void getHelp(String command, boolean detailed) {
        ACTIONS.get(command).usage(detailed);
    }
}
