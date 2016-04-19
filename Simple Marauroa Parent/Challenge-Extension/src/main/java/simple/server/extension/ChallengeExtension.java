package simple.server.extension;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.IRPRuleProcessor;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionProvider;
import simple.server.core.action.CommandCenter;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.event.TextEvent;
import simple.server.core.tool.Tool;

/**
 * This extension covers the challenging aspect of the game. Players challenge
 * opponents, accept or reject challenges
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProviders({
    @ServiceProvider(service = MarauroaServerExtension.class),
    @ServiceProvider(service = ActionProvider.class)})
public class ChallengeExtension extends SimpleServerExtension
        implements ActionProvider {

    /**
     * the logger instance.
     */
    private static final Logger LOG = Log4J.getLogger(ChallengeExtension.class);
    public static final String CHALLENGE = "Match_Challenge";
    public static final String ACCEPT_CHALLENGE = "Accept_Challenge";
    public static final String REJECT_CHALLENGE = "Reject_Challenge";
    public static final String CANCEL_CHALLENGE = "Cancel_Challenge";
    private static final String CHALLENGER = ChallengeEvent.CHALLENGER;
    private static final String CHALLENGED = ChallengeEvent.CHALLENGED;

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            LOG.debug("Action received: " + action);
            RPObject challenged
                    = (RPObject) ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class))
                    .getPlayer(action.get(CHALLENGED));
            if (challenged == null) {
                //Might be a NPC
                challenged = (RPObject) ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class)).getNPC(action.get(CHALLENGED));
            }
            ClientObjectInterface challenger
                    = ((SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class))
                    .getPlayer(action.get(CHALLENGER));
            if (action.get("type").equals(CHALLENGE)) {
                LOG.debug("Processing Challenge...");
                //Check both players exist
                if (challenged != null && challenger != null
                        && !Tool.extractName(challenged).equals(challenger.getName())) {
                    LOG.debug("Both players still exist. "
                            + "Send the Challenge to the challenged.");
                    switch (action.getInt(ChallengeEvent.ACTION)) {
                        case ChallengeEvent.CHALLENGE:
                            challenged.addEvent(new ChallengeEvent(action.get(CHALLENGER),
                                    action.get(CHALLENGED), ChallengeEvent.CHALLENGE));
                            break;
                        case ChallengeEvent.CANCEL:
                            LOG.info("Processing challenge cancel!");
                            break;
                        case ChallengeEvent.ACCEPT:
                            LOG.info("Processing challenge accept!");
                            break;
                        case ChallengeEvent.REJECT:
                            LOG.info("Processing challenge reject!");
                            break;
                        default:
                            LOG.warn("Unhandled action: "
                                    + action.getInt(ChallengeEvent.ACTION));
                    }
                    if (player instanceof RPObject) {
                        ((RPObject) player).addEvent(
                                new TextEvent("Command completed", "System"));
                    }
                    if (challenged instanceof ClientObjectInterface) {
                        ((ClientObjectInterface) challenged).notifyWorldAboutChanges();
                    } else {
                        Lookup.getDefault().lookup(IRPWorld.class).modify(challenged);
                    }
                    LOG.debug("Sent!");
                } else {
                    LOG.error("Something's wrong...");
                    LOG.error("Challenged: " + challenged);
                    LOG.error("Challenger: " + challenger);
                    switch (action.getInt(ChallengeEvent.ACTION)) {
                        case ChallengeEvent.CHALLENGE:
                            LOG.warn("Processing challenge challenge!");
                            break;
                        case ChallengeEvent.CANCEL:
                            LOG.warn("Processing challenge cancel!");
                            break;
                        case ChallengeEvent.ACCEPT:
                            LOG.warn("Processing challenge accept!");
                            break;
                        case ChallengeEvent.REJECT:
                            LOG.warn("Processing challenge reject!");
                            break;
                    }
                    if (player instanceof RPObject) {
                        ((RPObject) player).addEvent(
                                new TextEvent("Command not completed", "System"));
                    }
                }
            } else if (action.get("type").equals(ACCEPT_CHALLENGE)) {
                LOG.debug("Processing challenge accept...");
                challenger.addEvent(new ChallengeEvent(action.get(CHALLENGER),
                        action.get(CHALLENGED), ChallengeEvent.ACCEPT));
                challenger.notifyWorldAboutChanges();
            } else if (action.get("type").equals(REJECT_CHALLENGE)) {
                LOG.debug("Processing challenge reject...");
                challenger.addEvent(new ChallengeEvent(action.get(CHALLENGER),
                        action.get(CHALLENGED), ChallengeEvent.REJECT));
                challenger.notifyWorldAboutChanges();
            } else if (action.get("type").equals(CANCEL_CHALLENGE)) {
                LOG.debug("Processing challenge cancel...");
                //Notify challenged
                if (challenged != null) {
                    challenged.addEvent(new ChallengeEvent(action.get(CHALLENGER),
                            action.get(CHALLENGED), ChallengeEvent.CANCEL));
                    if (challenged instanceof ClientObjectInterface) {
                        ((ClientObjectInterface) challenged).notifyWorldAboutChanges();
                    } else {
                        Lookup.getDefault().lookup(IRPWorld.class).modify(challenged);
                    }
                }
            }
        }
    }

    @Override
    public void modifyClientObjectDefinition(RPClass player) {
        player.addRPEvent(ChallengeEvent.RPCLASS_NAME, Definition.VOLATILE);
    }

    @Override
    public String getName() {
        return "Challenge Extension";
    }

    public void register() {
        CommandCenter.register(CHALLENGE, ChallengeExtension.this);
        CommandCenter.register(ACCEPT_CHALLENGE, ChallengeExtension.this);
        CommandCenter.register(REJECT_CHALLENGE, ChallengeExtension.this);
        CommandCenter.register(CANCEL_CHALLENGE, ChallengeExtension.this);
    }
}
