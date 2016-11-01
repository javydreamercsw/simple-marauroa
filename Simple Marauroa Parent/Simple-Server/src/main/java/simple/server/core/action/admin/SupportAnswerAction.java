package simple.server.core.action.admin;

import marauroa.common.game.RPAction;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.Grammar;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import static simple.server.core.action.WellKnownActionConstant.TARGET;
import simple.server.core.engine.SimpleRPRuleProcessor;

@ServiceProvider(service = ActionProvider.class)
public class SupportAnswerAction extends AdministrationAction 
        implements ActionProvider {

    private static final String TEXT = "support_text";
    private static final String SUPPORTANSWER = "supportanswer";

    @Override
    public void register() {
        CommandCenter.register(SUPPORTANSWER, new SupportAnswerAction(), 50);
    }

    @Override
    public void perform(ClientObjectInterface player, RPAction action) {
        if (action.has(TARGET) && action.has(TEXT)) {
            final String message = player.getTitle() + " answers "
                    + Grammar.suffix_s(action.get(TARGET))
                    + " support question: " + action.get(TEXT);

            ((SimpleRPRuleProcessor) Lookup.getDefault()
                    .lookup(IRPRuleProcessor.class)).addGameEvent(player.getName(),
                    SUPPORTANSWER, action.get(TARGET),
                    action.get(TEXT));
            ClientObjectInterface supported
                    = ((SimpleRPRuleProcessor) Lookup.getDefault()
                            .lookup(IRPRuleProcessor.class))
                            .getPlayer(action.get(TARGET));
            if (supported != null) {

                supported.sendPrivateText("Support (" + player.getTitle()
                        + ") tells you: " + action.get(TEXT)
                        + " If you wish to reply, use /support.");
                supported.notifyWorldAboutChanges();
                SimpleRPRuleProcessor.sendMessageToSupporters(message);
            } else {
                player.sendPrivateText(action.get(TARGET)
                        + " is not currently logged in.");
            }
        }
    }
}
