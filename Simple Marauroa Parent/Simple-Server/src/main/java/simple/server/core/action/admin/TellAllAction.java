package simple.server.core.action.admin;

import marauroa.common.game.RPAction;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.SimpleRPRuleProcessor;

@ServiceProvider(service = ActionProvider.class)
public class TellAllAction extends AdministrationAction implements ActionProvider{

    private static final String _TEXT = "text";
    private static final String _TELLALL = "tellall";

    public void register() {
        CommandCenter.register(_TELLALL, new TellAllAction(), 200);
    }

    @Override
    public void perform(ClientObjectInterface player, RPAction action) {
        if (action.has(_TEXT)) {
            String message = "Administrator SHOUTS: " + action.get(_TEXT);
            ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class)).addGameEvent(player.getName(),
                    _TELLALL, action.get(_TEXT));

            ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class)).tellAllPlayers(message);
        }
    }
}
