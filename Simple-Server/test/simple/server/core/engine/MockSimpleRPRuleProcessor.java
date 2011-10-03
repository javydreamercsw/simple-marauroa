package simple.server.core.engine;

import simple.common.game.ClientObjectInterface;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class MockSimpleRPRuleProcessor extends SimpleRPRuleProcessor {

    public MockSimpleRPRuleProcessor() {
    }

    @Override
    public void addGameEvent(final String source, final String event, final String... params) {
        // do not log to database during test
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
