package simple.server.extension.d20.skill;

import java.util.ArrayList;
import java.util.List;
import marauroa.common.game.RPObject;
import simple.server.extension.d20.dice.DieEx;
import static simple.server.extension.d20.skill.D20Skill.modifiers;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.rpclass.D20Class;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractSkill extends RPObject implements D20Skill {

    private Double rank = 0.0;
    private List<Class<? extends D20Class>> exclusiveClasses
            = new ArrayList<>();
    private List<Class<? extends D20Skill>> requirements = new ArrayList<>();

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
    public Double getRank() {
        return rank;
    }

    @Override
    public void setRank(Double rank) {
        this.rank = rank;
    }
    
    @Override
    public String getCharacteristicName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getShortName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }
    
    @Override
    public String getDescription() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }
    
    @Override
    public List<Class<? extends D20Class>> getExclusiveClasses() {
        return exclusiveClasses;
    }

    @Override
    public List<Class<? extends D20Skill>> getRequirements() {
        return requirements;
    }
}
