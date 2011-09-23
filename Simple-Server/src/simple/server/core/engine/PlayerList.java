/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.engine;

import simple.common.filter.FilterCriteria;
import simple.common.game.ClientObjectInterface;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerList {

    public PlayerList() {
        players = new ConcurrentHashMap<String, ClientObjectInterface>();
    }
    private Map<String, ClientObjectInterface> players;

    /**
     * Retrieve from this list a player specified by its name.
     *
     * @param name
     *            the unique name of a player
     * @return the ClientObjectInterface specified by the name or <code> null </code> if not
     *         found
     */
    ClientObjectInterface getOnlinePlayer(String name) {
        return players.get(name.toLowerCase());
    }

    /**
     * Sends a privateText to all players in the list.
     *
     * @param message
     */
    void tellAllOnlinePlayers(final String message) {
        forAllPlayersExecute(new Task<ClientObjectInterface>() {

            @Override
            public void execute(ClientObjectInterface player) {
                player.sendPrivateText(message);
                player.notifyWorldAboutChanges();
            }
        });
    }

    /**
     * Calls the execute method of task for each player in this List.
     *
     * @param task
     *            the task to execute
     */
    public void forAllPlayersExecute(Task<ClientObjectInterface> task) {
        Iterator<Map.Entry<String, ClientObjectInterface>> it = players.entrySet().iterator();
        while (it.hasNext()) {
            task.execute(it.next().getValue());
        }
    }

    /**
     * Calls the execute method of task for all player in this list that return
     * true in filter.
     *
     * @param task
     *            the task to execute.
     * @param filter
     *            the FilterCriteria to pass
     */
    public void forFilteredPlayersExecute(Task<ClientObjectInterface> task, FilterCriteria<ClientObjectInterface> filter) {
        Iterator<Map.Entry<String, ClientObjectInterface>> it = players.entrySet().iterator();

        while (it.hasNext()) {

            ClientObjectInterface player = it.next().getValue();

            if (filter.passes(player)) {
                task.execute(player);
            }
        }
    }

    /**
     * The amount of currently logged in players.
     *
     * @return the amount ClientObjectInterface items in this list.
     */
    public int size() {
        return players.size();
    }

    public void add(ClientObjectInterface player) {
        String playerName = player.getName();

        if (playerName != null) {
            players.put(playerName.toLowerCase(), player);
        } else {
            throw new IllegalArgumentException("can't add player without name");
        }
    }

    public boolean remove(ClientObjectInterface player) {
        String playerName = player.getName();

        if (playerName != null) {
            return players.remove(playerName.toLowerCase()) != null;
        } else {
            throw new IllegalArgumentException("can't remove player without name:");
        }
    }
}
