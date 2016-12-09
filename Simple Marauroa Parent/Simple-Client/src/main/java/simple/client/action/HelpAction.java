package simple.client.action;

import simple.client.SimpleUI;
import simple.common.NotificationType;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class HelpAction implements SlashAction {

    private final ActionRepository repo;
    private final String[] lines;
    private final SimpleUI ui;

    /**
     * Help action class
     *
     * @param repo Repository containing the actions handled by this class
     * @param lines Additional lines of text to be displayed before the actual
     * usage. Usually for usage with no parameters.
     * @param ui
     */
    public HelpAction(ActionRepository repo, String[] lines, SimpleUI ui) {
        this.repo = repo;
        this.lines = lines;
        this.ui = ui;
    }

    /**
     * Execute a chat command.
     *
     * @param params The formal parameters.
     * @param remainder Line content after parameters.
     *
     * @return <code>true</code> if was handled.
     */
    @SuppressWarnings("static-access")
    @Override
    public boolean execute(String[] params, String remainder) {
        String command = params[1];
        boolean detailed = false;
        if (params.length == 3) {
            detailed = params[2].equals("-d");
        } else if (params.length == 2) {
            detailed = params[1].equals("-d");
        }
        if (lines != null && params.length == 1) {
            for (String line : lines) {
                ui.get().addEventLine(line, NotificationType.CLIENT);
            }
            repo.getHelpMessages(detailed);
        } else {
            repo.getHelp(command, detailed);
        }
        return true;
    }

    /**
     * Get the maximum number of formal parameters.
     *
     * @return The parameter count.
     */
    @Override
    public int getMaximumParameters() {
        // /help <command>
        return 2;
    }

    /**
     * Get the minimum number of formal parameters.
     *
     * @return The parameter count.
     */
    @Override
    public int getMinimumParameters() {
        //In case they just type /help
        return 1;
    }

    /**
     * Show usage for this command.
     *
     */
    @SuppressWarnings("static-access")
    @Override
    public void usage(boolean detailed) {
        ui.get().addEventLine("- /help <command> -d"
                + (detailed ? "\t\tGet detailed help for the named command." : "")
                + "- /help <command>"
                + (detailed ? "\t\tGet non-detailed help for the named command." : ""),
                NotificationType.CLIENT);
    }
}
