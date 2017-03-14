package simple.server.core.entity.npc.action;

import java.util.List;
import marauroa.common.game.RPEvent;
import simple.server.core.entity.npc.NPC;

/**
 * This represents an action a NPC responds to.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface NPCAction {

    /**
     * This is the keyword this action reacts to. i.e. 'help'
     *
     * @return keyword for this action.
     */
    public List<String> getKeywords();

    /**
     * React to the keyword.
     *
     * @param event Event providing additional details.
     * @param npc NPC doing the action.
     * @return RPEvent to send back as a public event. Return null to do nothing
     * public.
     */
    public RPEvent onAction(RPEvent event, NPC npc);

    /**
     * Action's description.
     *
     * @return Action's description.
     */
    public String getDescription();
}
