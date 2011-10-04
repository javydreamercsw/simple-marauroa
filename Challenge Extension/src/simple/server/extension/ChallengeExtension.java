package simple.server.extension;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.extension.MarauroaServerExtension;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionListener;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.event.TextEvent;

/**
 * This extension covers the challenging aspect of the game. Players challenge
 * opponents, accept or reject challenges
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class ChallengeExtension extends SimpleServerExtension implements ActionListener {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(ChallengeExtension.class);
    public static final String _CHALLENGE = "Match_Challenge";
    public static final String _ACCEPT_CHALLENGE = "Accept_Challenge";
    public static final String _REJECT_CHALLENGE = "Reject_Challenge";
    public static final String _CANCEL_CHALLENGE = "Cancel_Challenge";
    private static final String _CHALLENGER = ChallengeEvent.CHALLENGER;
    private static final String _CHALLENGED = ChallengeEvent.CHALLENGED;

    @Override
    public void init() {
        CommandCenter.register(_CHALLENGE, this);
        CommandCenter.register(_ACCEPT_CHALLENGE, this);
        CommandCenter.register(_REJECT_CHALLENGE, this);
        CommandCenter.register(_CANCEL_CHALLENGE, this);
        ChallengeEvent.generateRPClass();
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            logger.debug("Action received: " + action);
            ClientObjectInterface challenged = ((SimpleRPRuleProcessor)Lookup.getDefault().lookup(IRPRuleProcessor.class)).getPlayer(action.get(_CHALLENGED));
            ClientObjectInterface challenger = ((SimpleRPRuleProcessor)Lookup.getDefault().lookup(IRPRuleProcessor.class)).getPlayer(action.get(_CHALLENGER));
            if (action.get("type").equals(_CHALLENGE)) {
                logger.debug("Processing Challenge...");
                //Check both players exist
                if (challenged != null && challenger != null) {
                    logger.debug("Both players still exist. Send the Challenge to the challenged.");
                    challenged.addEvent(new ChallengeEvent(action.get(_CHALLENGER), action.get(_CHALLENGED), ChallengeEvent.CHALLENGE));
                    if (player != null) {
                        ((RPObject) player).addEvent(new TextEvent("Command completed", "System"));
                    }
                    challenged.notifyWorldAboutChanges();
                    logger.debug("Sent!");
                } else {
                    logger.error("Something's wrong...");
                    logger.error("Challenged: " + challenged);
                    logger.error("Challenger: " + challenger);
                }
            } else if (action.get("type").equals(_ACCEPT_CHALLENGE)) {
                logger.debug("Processing challenge accept...");
                challenger.addEvent(new ChallengeEvent(action.get(_CHALLENGER), action.get(_CHALLENGED), ChallengeEvent.ACCEPT));
                challenger.notifyWorldAboutChanges();
            } else if (action.get("type").equals(_REJECT_CHALLENGE)) {
                logger.debug("Processing challenge reject...");
                challenger.addEvent(new ChallengeEvent(action.get(_CHALLENGER), action.get(_CHALLENGED), ChallengeEvent.REJECT));
                challenger.notifyWorldAboutChanges();
            } else if (action.get("type").equals(_CANCEL_CHALLENGE)) {
                logger.debug("Processing challenge cancel...");
                //Notify challenged
                if (challenged != null) {
                    challenged.addEvent(new ChallengeEvent(action.get(_CHALLENGER), action.get(_CHALLENGED), ChallengeEvent.CANCEL));
                    challenged.notifyWorldAboutChanges();
                }
            }
        }
    }

    @Override
    public void modifyClientObjectDefinition(RPClass player) {
        player.addRPEvent(ChallengeEvent.RPCLASS_NAME, Definition.VOLATILE);
    }
}
