
package simple.server.core.action.admin;

import marauroa.common.game.RPAction;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleSingletonRepository;

public class TellAllAction extends AdministrationAction {

    private static final String _TEXT = "text";
    private static final String _TELLALL = "tellall";

    public static void register() {
        CommandCenter.register(_TELLALL, new TellAllAction(), 200);
    }

    @Override
    public void perform(ClientObjectInterface player, RPAction action) {
        if (action.has(_TEXT)) {
            String message = "Administrator SHOUTS: " + action.get(_TEXT);
            SimpleSingletonRepository.get().get(SimpleRPRuleProcessor.class).addGameEvent(player.getName(),
                    _TELLALL, action.get(_TEXT));

            SimpleSingletonRepository.get().get(SimpleRPRuleProcessor.class).tellAllPlayers(message);
        }
    }
}
