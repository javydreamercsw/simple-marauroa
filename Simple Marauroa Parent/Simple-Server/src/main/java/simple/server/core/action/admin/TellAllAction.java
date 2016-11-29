package simple.server.core.action.admin;

import marauroa.common.game.RPAction;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.entity.RPEntityInterface;

@ServiceProvider(service = ActionProvider.class)
public class TellAllAction extends AdministrationAction implements ActionProvider {

    private static final String TEXT = "text";
    private static final String TELLALL = "tellall";

    @Override
    public void register() {
        CommandCenter.register(TELLALL, new TellAllAction(), 200);
    }

    @Override
    public void perform(RPEntityInterface player, RPAction action) {
        if (action.has(TEXT)) {
            String message = "Administrator SHOUTS: " + action.get(TEXT);
            ((SimpleRPRuleProcessor) Lookup.getDefault()
                    .lookup(IRPRuleProcessor.class))
                    .addGameEvent(player.getName(),
                            TELLALL, action.get(TEXT));

            ((SimpleRPRuleProcessor) Lookup.getDefault()
                    .lookup(IRPRuleProcessor.class))
                    .tellAllPlayers(message);
        }
    }
}
