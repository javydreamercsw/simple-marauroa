package simple.server.extension.d20.list;

import marauroa.common.game.Definition;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = D20List.class)
public class SkillList extends AbstractList {

    public static final String SKILL = "skills";

    @Override
    public int getSize() {
        return -1;
    }

    @Override
    public String getCharacteristicName() {
        return SKILL;
    }

    @Override
    public String getShortName() {
        return SKILL;
    }

    @Override
    public String getDescription() {
        return "List of skills for this wrestler.";
    }
    
    @Override
    public Byte getDefinition() {
        return Definition.PRIVATE;
    }
}
