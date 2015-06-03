package simple.server.mock;

import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.PlayerList;
import simple.server.core.engine.SimpleRPRuleProcessor;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class MockSimpleRPRuleProcessor extends SimpleRPRuleProcessor {

    public MockSimpleRPRuleProcessor() {
    }

    @Override
    public int getTurn() {
        return 0;
    }

    /**
     * Adds a player object to the list of players.
     *
     * @param player Player
     */
    public void addPlayer(final ClientObjectInterface player) {
        this.onlinePlayers.add(player);
    }

    /**
     * reset the list of online players.
     */
    public void clearPlayers() {
        onlinePlayers = new PlayerList();
    }
}
