package simple.server.core.entity.clientobject;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.event.ILoginNotifier;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.LoginListener;
import simple.server.core.event.TurnListener;
import simple.server.util.TimeUtil;

/**
 * Manages gags.
 */
@ServiceProvider(service = LoginListener.class)
public class GagManager implements LoginListener {

    private static final Logger logger = Log4J.getLogger(GagManager.class);

    public GagManager() {
        Lookup.getDefault().lookup(ILoginNotifier.class).addListener(GagManager.this);
    }

    /**
     * @param criminalName The name of the player who should be gagged
     * @param policeman The name of the admin who wants to gag the criminal
     * @param minutes The duration of the sentence
     * @param reason why criminal was gagged
     */
    @Override
    public void gag(final String criminalName, ClientObjectInterface policeman, 
            int minutes,
            String reason) {
        final ClientObjectInterface criminal
                = ((SimpleRPRuleProcessor) Lookup.getDefault()
                        .lookup(IRPRuleProcessor.class)).getPlayer(criminalName);

        if (criminal == null) {
            String text = "ClientObjectInterface " + criminalName 
                    + " not found";
            policeman.sendPrivateText(text);
            logger.debug(text);
            return;
        }

        gag(criminal, policeman, minutes, reason, criminalName);
    }

    void gag(final ClientObjectInterface criminal, 
            ClientObjectInterface policeman, int minutes,
            String reason, final String criminalName) {
        // no -1
        if (minutes < 0) {
            policeman.sendPrivateText("Infinity (negative numbers) is not supported.");
            return;
        }

        // Set the gag
        long expireDate = System.currentTimeMillis() 
                + (1000L * 60L * minutes); // Milliseconds

        ((RPObject) criminal).put("gag", "" + expireDate);

        // Send messages
        policeman.sendPrivateText("You have gagged " + criminalName 
                + " for " + minutes + " minutes. Reason: " + reason + ".");
        criminal.sendPrivateText("You have been gagged by " 
                + policeman.getTitle() + " for " + minutes + " minutes. Reason: " + reason + ".");
        SimpleRPRuleProcessor.sendMessageToSupporters("GagManager", 
                policeman.getName() + " gagged " + criminalName + " for " 
                        + minutes + " minutes. Reason: " + reason + ".");

        setupNotifier(criminal);
    }

    /**
     * Removes a gag.
     *
     * @param inmate player who should be released
     */
    public void release(ClientObjectInterface inmate) {

        if (isGagged(inmate)) {
            ((RPObject) inmate).put("gag", "" + 0);
            inmate.sendPrivateText("Your gag sentence is over.");
            logger.debug("ClientObjectInterface " + inmate.getName()
                    + "released from gag.");
        }
    }

    /**
     * Is player gagged?
     *
     * @param player player to check
     * @return true, if it is gagged, false otherwise.
     */
    public static boolean isGagged(ClientObjectInterface player) {
        return Long.parseLong(((RPObject) player).get("gag")) > 0;
    }

    /**
     * Like isGagged(player) but informs the player in case it is gagged.
     *
     * @param player player to check
     * @return true, if it is gagged, false otherwise.
     */
    @Override
    public boolean checkIsGaggedAndInformPlayer(ClientObjectInterface player) {
        boolean res = GagManager.isGagged(player);
        if (res) {
            long timeRemaining = getTimeRemaining(player);
            player.sendPrivateText("You are gagged, it will expire in " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)));
        }

        return res;
    }

    /**
     * If the players' gag has expired, remove it.
     *
     * @param player player to check
     * @return true, if the gag expired and was removed or was already removed.
     * false, if the player still has time to serve.
     */
    private boolean tryExpire(ClientObjectInterface player) {
        if (!isGagged(player)) {
            return true;
        }

        // allow for an error of 10 seconds
        if (getTimeRemaining(player) < (10L * 1000L)) {
            release(player);
            return true;
        }

        return false;
    }

    @Override
    public void onLoggedIn(ClientObjectInterface player) {
        if (!isGagged(player)) {
            return;
        }

        if (!tryExpire(player)) {
            setupNotifier(player);
        }
    }

    private void setupNotifier(ClientObjectInterface criminal) {

        final String criminalName = criminal.getName();

        // Set a timer so that the inmate is automatically released after
        // serving his sentence. We're using the TurnNotifier; we use
        Lookup.getDefault().lookup(ITurnNotifier.class).notifyInSeconds(
                (int) (getTimeRemaining(criminal) / 1000), new TurnListener() {

                    @Override
                    public void onTurnReached(int currentTurn) {

                        ClientObjectInterface criminal2
                        = ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class)).getPlayer(criminalName);
                        if (criminal2 == null) {
                            logger.debug("Gagged player " + criminalName + "has logged out.");
                            return;
                        }

                        tryExpire(criminal2);

                    }
                });
    }

    /**
     * Gets time remaining in milliseconds.
     *
     * @param criminal player to check
     * @return time remaining in milliseconds
     */
    @Override
    public long getTimeRemaining(ClientObjectInterface criminal) {
        if (!isGagged(criminal)) {
            return 0L;
        }
        long expireDate = Long.parseLong(((RPObject) criminal).get("gag"));
        long timeRemaining = expireDate - System.currentTimeMillis();
        return timeRemaining;
    }
}
