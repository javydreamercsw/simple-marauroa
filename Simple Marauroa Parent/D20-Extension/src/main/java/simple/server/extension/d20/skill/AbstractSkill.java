package simple.server.extension.d20.skill;

import simple.server.extension.d20.DieEx;
import static simple.server.extension.d20.skill.D20Skill.modifiers;
import simple.server.extension.d20.ability.D20Ability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractSkill implements D20Skill {

    private int rank = 0;

    @Override
    public boolean isModifiesAttribute(Class<? extends D20Ability> attr) {
        return modifiers.containsKey(attr);
    }

    @Override
    public int getModifier(Class<? extends D20Ability> attr) {
        int result = 0;
        if (modifiers.containsKey(attr)) {
            String eq = modifiers.get(attr);
            if (eq.contains("d")) {
                result = new DieEx(eq).roll();
            } else {
                result = Integer.parseInt(eq);
            }
        }
        return result;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    @Override
    public String getName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getShortName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }
}
