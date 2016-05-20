package simple.server.extension.d20.rpclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import simple.server.core.entity.RPEntity;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.level.D20Level;
import simple.server.extension.d20.skill.D20Skill;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractClass extends RPEntity implements D20Class {

    public final static String RP_CLASS = "Abstract Class";
    protected int bonusSkillPoints = 0, bonusFeatPoints = 0;
    //Ability, Bonus
    private final Map<Class<? extends D20Ability>, Integer> bonuses
            = new HashMap<>();
    //Feat, level when is available.
    private final List<Class<? extends D20Feat>> preferredFeats = new ArrayList<>();
    //Feat, level when is gained.
    private final Map<Class<? extends D20Feat>, Integer> bonusFeats
            = new HashMap<>();
    private final List<Class<? extends D20Skill>> preferredSkills
            = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(AbstractClass.class.getSimpleName());
    private final Map<Class<? extends D20Skill>, Integer> bonusSkills
            = new HashMap<>();

    public AbstractClass(RPObject object) {
        super(object);
        update();
    }

    public AbstractClass() {
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RP_CLASS)) {
            try {
                RPClass clazz = new RPClass(RP_CLASS);
                clazz.isA(RPEntity.class.newInstance().getRPClassName());
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        if (!RPCLASS_NAME.isEmpty() && !RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass clazz = new RPClass(RPCLASS_NAME);
            clazz.isA(RP_CLASS);
        }
    }

    @Override
    public Map<Class<? extends D20Ability>, Integer> getAttributeBonuses() {
        return bonuses;
    }

    @Override
    public Map<Class<? extends D20Feat>, Integer> getBonusFeats() {
        return bonusFeats;
    }

    @Override
    public List<Class<? extends D20Feat>> getPrefferedFeats() {
        return preferredFeats;
    }

    @Override
    public List<Class<? extends D20Skill>> getPrefferedSkills() {
        return preferredSkills;
    }

    @Override
    public int getBonusSkillPoints(int level) {
        return bonusSkillPoints;
    }

    @Override
    public int getBonusFeatPoints(int level) {
        return bonusFeatPoints;
    }

    @Override
    public Map<Class<? extends D20Skill>, Integer> getBonusSkills() {
        return bonusSkills;
    }

    @Override
    public int getLevel() {
        return getInt(D20Level.LEVEL);
    }

    @Override
    public void setLevel(int level) {
        put(D20Level.LEVEL, level);
    }

    @Override
    public int getMaxLevel() {
        return -1;
    }

    @Override
    public void setMaxLevel(int max) {
        //Do nothing
    }
}
