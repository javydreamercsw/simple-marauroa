package simple.client.action;

import java.util.HashMap;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ActionRepository {

    /** Set of client supported Actions. */
    private static HashMap<String, SlashAction> actions = new HashMap<String, SlashAction>();

    /**
     * Registers the available Action.
     * @param command Unique command for this action
     * @param action Action class to handle the command
     */
    public static void register(String command, SlashAction action) {
        actions.put(command, action);
    }

    /**
     * Gets the Action object for the specified Action name.
     *
     * @param name
     *            name of Action
     * @return Action object
     */
    public static SlashAction get(String name) {
        return actions.get(name);
    }

    /**
     * Gets the help messages of all registered Actions.
     *
     * @param detailed Additional details of the command usage
     */
    public static void getHelpMessages(boolean detailed) {
        for (SlashAction action : actions.values()) {
            action.usage(detailed);
        }
    }

    /**
     * Gets the help messages for the action of the matching Actions.
     *
     * @param command
     *            Name of Action
     * @param detailed
     *            Additional details of the command usage
     */
    public static void getHelp(String command, boolean detailed) {
        actions.get(command).usage(detailed);
    }
}
