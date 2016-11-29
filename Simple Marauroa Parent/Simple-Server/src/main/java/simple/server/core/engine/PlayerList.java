package simple.server.core.engine;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.common.NotificationType;
import simple.common.filter.FilterCriteria;
import simple.common.game.ClientObjectInterface;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.tool.Tool;

public class PlayerList {

    private final Map<String, RPEntityInterface> players;

    public PlayerList() {
        players = new ConcurrentHashMap<>();
    }

    /**
     * Retrieve from this list a player specified by its name.
     *
     * @param name the unique name of a player
     * @return the ClientObjectInterface specified by the name or <code> null
     * </code> if not found
     */
    RPEntityInterface getOnlinePlayer(String name) {
        return players.get(name.toLowerCase());
    }

    /**
     * Sends a privateText to all players in the list.
     *
     * @param message
     */
    void tellAllOnlinePlayers(final String message) {
        forAllPlayersExecute((RPEntityInterface player) -> {
            if (player instanceof ClientObjectInterface) {
                ClientObjectInterface coi = (ClientObjectInterface) player;
                coi.sendPrivateText(message);
                coi.notifyWorldAboutChanges();
            } else {
                ((RPObject) player).addEvent(
                        new PrivateTextEvent(NotificationType.PRIVMSG, message));
                Lookup.getDefault().lookup(IRPWorld.class)
                        .modify((RPObject) player);
            }
        });
    }

    /**
     * Calls the execute method of task for each player in this List.
     *
     * @param task the task to execute
     */
    public void forAllPlayersExecute(Task<RPEntityInterface> task) {
        Iterator<Map.Entry<String, RPEntityInterface>> it
                = players.entrySet().iterator();
        while (it.hasNext()) {
            task.execute(it.next().getValue());
        }
    }

    /**
     * Calls the execute method of task for all player in this list that return
     * true in filter.
     *
     * @param task the task to execute.
     * @param filter the FilterCriteria to pass
     */
    public void forFilteredPlayersExecute(Task<RPEntityInterface> task,
            FilterCriteria<RPEntityInterface> filter) {
        Iterator<Map.Entry<String, RPEntityInterface>> it
                = players.entrySet().iterator();
        while (it.hasNext()) {

            RPEntityInterface player = it.next().getValue();

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

    public void add(RPEntityInterface player) {
        String playerName = Tool.extractName((RPObject) player);

        if (playerName != null) {
            players.put(playerName.toLowerCase(), player);
        } else {
            throw new IllegalArgumentException("Can't add player without name!");
        }
    }

    public boolean remove(RPEntityInterface player) {
        String playerName = Tool.extractName((RPObject) player);

        if (playerName != null) {
            return players.remove(playerName.toLowerCase()) != null;
        } else {
            throw new IllegalArgumentException("Can't remove player without name!");
        }
    }
}
