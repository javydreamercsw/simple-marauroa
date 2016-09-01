package simple.server.core.entity.npc;

import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.server.core.entity.npc.action.NPCAction;
import simple.server.core.entity.api.MonitoreableEntity;

/**
 * NPC's that talk with players.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public abstract class SpeakerNPC extends NPC implements MonitoreableEntity {

    private static final Logger LOG
            = Logger.getLogger(SpeakerNPC.class.getSimpleName());

    public SpeakerNPC() {
        loadDefaultActions();
    }

    public SpeakerNPC(RPObject object) {
        super(object);
        loadDefaultActions();
    }

    private void loadDefaultActions() {
        //Load the default actions
        for (NPCAction a : Lookup.getDefault().lookupAll(NPCAction.class)) {
            context.add(a);
        }
        //Subclasses should add actions on creation.
        loadCustomActions();
    }

    /**
     * Custom loading of actions.
     */
    protected abstract void loadCustomActions();
}
