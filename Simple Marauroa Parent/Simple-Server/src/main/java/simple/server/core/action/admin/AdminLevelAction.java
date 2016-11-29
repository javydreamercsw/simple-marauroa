package simple.server.core.action.admin;

import java.util.logging.Level;
import marauroa.common.game.RPAction;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import static simple.server.core.action.WellKnownActionConstant.TARGET;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.entity.RPEntityInterface;

@ServiceProvider(service = ActionProvider.class)
public class AdminLevelAction extends AdministrationAction
        implements ActionProvider {

    private static final String ADMINLEVEL = "adminlevel";
    private static final String NEWLEVEL = "newlevel";

    @Override
    public void register() {
        CommandCenter.register(ADMINLEVEL, new AdminLevelAction(), 0);
    }

    @Override
    public void perform(RPEntityInterface player, RPAction action) {

        if (action.has(TARGET)) {
            String name = action.get(TARGET);
            RPEntityInterface target
                    = ((SimpleRPRuleProcessor) Lookup.getDefault()
                            .lookup(IRPRuleProcessor.class)).getPlayer(name);
            if (target == null || (target.isGhost() && !isAllowedtoSeeGhosts(player))) {
                LOG.log(Level.FINE, "Player \"{0}\" not found", name);
                player.sendPrivateText("Player \"" + name + "\" not found");
                return;
            }
            int oldlevel = target.getAdminLevel();
            String response = target.getTitle() + " has adminlevel " + oldlevel;
            if (action.has(NEWLEVEL)) {
                // verify newlevel is a number
                int newlevel;
                try {
                    newlevel = Integer.parseInt(action.get(NEWLEVEL));
                }
                catch (NumberFormatException e) {
                    player.sendPrivateText("The new adminlevel needs to be an Integer");
                    return;
                }
                if (newlevel > REQUIRED_ADMIN_LEVEL_FOR_SUPER) {
                    newlevel = REQUIRED_ADMIN_LEVEL_FOR_SUPER;
                }
                int mylevel = player.getAdminLevel();
                if (mylevel < REQUIRED_ADMIN_LEVEL_FOR_SUPER) {
                    response = "Sorry, but you need an adminlevel of "
                            + REQUIRED_ADMIN_LEVEL_FOR_SUPER
                            + " to change adminlevel.";
                } else {
                    // OK, do the change
                    Lookup.getDefault().lookup(SimpleRPRuleProcessor.class)
                            .addGameEvent(player.getName(), ADMINLEVEL,
                                    target.getName(), ADMINLEVEL,
                                    action.get(NEWLEVEL));
                    target.setAdminLevel(newlevel);
                    target.update();
                    target.notifyWorldAboutChanges();
                    response = "Changed adminlevel of " + target.getTitle()
                            + " from " + oldlevel + " to " + newlevel + ".";
                    target.sendPrivateText(player.getTitle()
                            + " changed your adminlevel from " + +oldlevel
                            + " to " + newlevel + ".");
                }
            }
            player.sendPrivateText(response);
        }
    }

    boolean isAllowedtoSeeGhosts(RPEntityInterface player) {
        return AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player,
                "ghostmode", false);
    }
}
