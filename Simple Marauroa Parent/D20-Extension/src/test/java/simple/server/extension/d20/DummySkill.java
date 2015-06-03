package simple.server.extension.d20;

import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.skill.AbstractSkill;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DummySkill extends AbstractSkill {

    public DummySkill() {
        modifiers.clear();
        modifiers.put(DummyAbility.class, "1");
        modifiers.put(DummyAbility2.class, "2d4+1");
    }

    @Override
    public String getCharacteristicName() {
        return "Dummy Ability";
    }

    @Override
    public String getShortName() {
        return "DA";
    }

    @Override
    public Class<? extends D20Ability> getAbility() {
        return DummyAbility.class;
    }
    
    @Override
    public String getDescription() {
        return "Dummy";
    }
}
