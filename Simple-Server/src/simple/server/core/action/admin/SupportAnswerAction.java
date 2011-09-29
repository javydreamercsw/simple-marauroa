/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.action.admin;

import marauroa.common.game.RPAction;
import simple.common.Grammar;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.CommandCenter;
import static simple.server.core.action.WellKnownActionConstant.TARGET;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleSingletonRepository;

public class SupportAnswerAction extends AdministrationAction {

  private static final String _TEXT = "text";
  private static final String _SUPPORTANSWER = "supportanswer";

  public static void register() {
    CommandCenter.register(_SUPPORTANSWER, new SupportAnswerAction(), 50);
  }

  @Override
  public void perform(ClientObjectInterface player, RPAction action) {
    if (action.has(TARGET) && action.has(_TEXT)) {
      final String message = player.getTitle() + " answers " + Grammar.suffix_s(action.get(TARGET)) + " support question: " + action.get(_TEXT);

      SimpleSingletonRepository.get().get(SimpleRPRuleProcessor.class).addGameEvent(player.getName(), _SUPPORTANSWER, action.get(TARGET),
              action.get(_TEXT));
      ClientObjectInterface supported = SimpleSingletonRepository.get().get(SimpleRPRuleProcessor.class).getPlayer(action.get(TARGET));
      if (supported != null) {

        supported.sendPrivateText("Support (" + player.getTitle() + ") tells you: " + action.get(_TEXT) + " If you wish to reply, use /support.");
        supported.notifyWorldAboutChanges();
        SimpleRPRuleProcessor.sendMessageToSupporters(message);
      } else {
        player.sendPrivateText(action.get(TARGET) + " is not currently logged in.");
      }
    }
  }
}
