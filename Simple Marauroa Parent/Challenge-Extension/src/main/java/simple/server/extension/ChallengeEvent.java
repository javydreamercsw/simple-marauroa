package simple.server.extension;

import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.event.SimpleRPEvent;
import simple.server.core.event.api.IRPEvent;

/**
 * This is the Challenge event
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IRPEvent.class)
public class ChallengeEvent extends SimpleRPEvent {

    public static final String RPCLASS_NAME = "challenge_event";
    public static final String CHALLENGER = "challenger", ACTION = "action", 
            CHALLENGED = "challenged";
    public static final int CHALLENGE = 1, ACCEPT = 2, REJECT = 3, CANCEL = 4;

    /**
     * Creates the rpclass.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass rpclass = new RPClass(RPCLASS_NAME);
            rpclass.add(DefinitionClass.ATTRIBUTE, CHALLENGER, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, CHALLENGED, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, ACTION, Type.INT);
        }
    }

    @Override
    public String getRPClassName() {
        return RPCLASS_NAME;
    }

    /**
     * @return the CHALLENGER
     */
    public String getChallenger() {
        return get(CHALLENGER);
    }

    /**
     * @return the ACTION
     */
    public static String getAction() {
        return ACTION;
    }

    /**
     * @return the CHALLENGED
     */
    public String getChallenged() {
        return get(CHALLENGED);
    }

    public ChallengeEvent() {
        super(RPCLASS_NAME);
    }

    /**
     * Creates a new challenge event.
     *
     * @param challenger player challenging
     * @param challenged player challenged
     * @param action either add or remove
     */
    public ChallengeEvent(String challenger, String challenged, int action) {
        super(RPCLASS_NAME);
        put(CHALLENGER, challenger);
        put(CHALLENGED, challenged);
        put(ACTION, action);
    }
}
