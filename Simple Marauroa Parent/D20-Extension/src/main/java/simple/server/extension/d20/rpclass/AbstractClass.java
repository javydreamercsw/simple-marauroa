package simple.server.extension.d20.rpclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import simple.server.core.entity.RPEntity;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.dice.DiceParser;
import simple.server.extension.d20.dice.DieRoll;
import simple.server.extension.d20.feat.D20Feat;
import simple.server.extension.d20.level.D20Level;
import simple.server.extension.d20.list.FeatList;
import simple.server.extension.d20.list.SkillList;
import simple.server.extension.d20.misc.D20Misc;
import simple.server.extension.d20.skill.D20Skill;
import simple.server.extension.d20.stat.D20Stat;
import simple.server.extension.d20.stat.Hit_Point;

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

    @Override
    public void update() {
        super.update();
        for (D20Ability a : Lookup.getDefault().lookupAll(D20Ability.class)) {
            if (!has(a.getCharacteristicName())) {
                put(a.getCharacteristicName(), 0);
            }
        }
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RP_CLASS)) {
            try {
                RPClass clazz = new RPClass(RP_CLASS);
                clazz.isA(RPEntity.class.newInstance().getRPClassName());
                for (D20Ability a : Lookup.getDefault().lookupAll(D20Ability.class)) {
                    clazz.addAttribute(a.getCharacteristicName(),
                            Definition.Type.INT);
                }
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

    @Override
    public void initialRolls() {
        //If HP is 0, do the initial roll
        String name = new Hit_Point().getCharacteristicName();
        if (has(name) && getInt(name) == 0) {
            int result = 0;
            List<DieRoll> parseRoll = DiceParser.parseRoll("6" + getHPDice());
            for (DieRoll roll : parseRoll) {
                result += roll.makeRoll().getTotal();
            }
            put(name, result);
            for (D20Ability ability : Lookup.getDefault().lookupAll(D20Ability.class)) {
                if (has(ability.getCharacteristicName())
                        && getInt(ability.getCharacteristicName()) == 0) {
                    /**
                     * Calculate ability scores. Roll 4 6-sided die and record
                     * the cumulative total of the highest 3 dice for each
                     * ability.
                     */
                    int r = 0;
                    Integer[] rolls = new Integer[4];
                    for (int count = 0; count < 4; count++) {
                        List<DieRoll> pr = DiceParser.parseRoll("d6");
                        for (DieRoll roll : pr) {
                            rolls[count] = roll.makeRoll().getTotal();
                            LOG.log(Level.FINE, "Roll #{0}={1}",
                                    new Object[]{(count + 1), rolls[count]});
                        }
                    }
                    //Now sort it
                    Arrays.sort(rolls, Collections.reverseOrder());
                    //Now use the first 3
                    for (int i = 0; i < 3; i++) {
                        LOG.log(Level.FINE, "Using result:{0}", rolls[i]);
                        r += rolls[i];
                    }
                    Integer bonus = getAttributeBonuses().get(ability.getClass());
                    //Apply any race bonuses
                    if (getAttributeBonuses().containsKey(ability.getClass())) {
                        LOG.log(Level.FINE, "Adding race {0} to: {1} ({2})",
                                new Object[]{bonus > 0 ? "bonus" : "penalty",
                                    ability.getCharacteristicName(), bonus});
                        r += bonus;
                    }
                    //Make sure penalties didn't get it lower than 0.
                    if (r < 0) {
                        r = 0;
                    }
                    put(ability.getCharacteristicName(), r);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getName()).append("\n");
        sb.append("Miscellaneous--------------------------------").append("\n");
        for (D20Misc misc : Lookup.getDefault().lookupAll(D20Misc.class)) {
            sb.append(misc.getShortName()).append(": ")
                    .append(get(misc.getCharacteristicName()))
                    .append("\n");
        }
        sb.append("Abilities------------------------------------").append("\n");
        for (D20Ability ability : Lookup.getDefault().lookupAll(D20Ability.class)) {
            try {
                sb.append(ability.getShortName()).append(": ")
                        .append(getInt(ability.getCharacteristicName()))
                        .append(" Mod: ")
                        .append(getAbilityModifier(ability.getClass()))
                        .append("\n");
            } catch (InstantiationException | IllegalAccessException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        sb.append("Stats----------------------------------------").append("\n");
        for (D20Stat stat : Lookup.getDefault().lookupAll(D20Stat.class)) {
            sb.append(stat.getShortName()).append(": ");
            if (stat.getDefinitionType() == Definition.Type.INT) {
                sb.append(getInt(stat.getCharacteristicName()));
            } else {
                sb.append(get(stat.getCharacteristicName()));
            }
            sb.append("\n");
        }
        if (hasSlot(FeatList.FEAT) && getSlot(FeatList.FEAT).size() > 0) {
            sb.append("Feats----------------------------------------").append("\n");
            for (RPObject o : getSlot(FeatList.FEAT)) {
                D20Feat feat = (D20Feat) o;
                sb.append(feat.getShortName()).append("\n");
            }
        }
        if (hasSlot(SkillList.SKILL) && getSlot(SkillList.SKILL).size() > 0) {
            sb.append("Skills----------------------------------------").append("\n");
            for (RPObject o : getSlot(SkillList.SKILL)) {
                D20Skill skill = (D20Skill) o;
                sb.append(skill.getShortName())
                        .append(" Rank: ")
                        .append(skill.getRank())
                        .append("\n");
            }
        }
        return sb.toString();
    }

    public final int getAbilityModifier(Class<? extends D20Ability> ability)
            throws InstantiationException, IllegalAccessException {
        D20Ability a = ability.newInstance();
        int result = 0;
        if (has(a.getCharacteristicName())) {
            result = (int) Math.floor((getInt(a.getCharacteristicName()) - 10) / 2);
        }
        return result;
    }
}
