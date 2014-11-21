package simple.server.extension.skill;

import simple.server.extension.DieEx;
import static simple.server.extension.skill.D20Skill.modifiers;
import simple.server.extension.attribute.D20Attribute;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractSkill implements D20Skill {

    @Override
    public boolean isModifiesAttribute(Class<? extends D20Attribute> attr) {
        return modifiers.containsKey(attr);
    }

    @Override
    public int getModifier(Class<? extends D20Attribute> attr) {
        int result = 0;
        if (modifiers.containsKey(attr)) {
            String eq = modifiers.get(attr);
            if (eq.contains("d")) {
                result = new DieEx(eq).roll();
            }else{
                result = Integer.parseInt(eq);
            }
        }
        return result;
    }
}
