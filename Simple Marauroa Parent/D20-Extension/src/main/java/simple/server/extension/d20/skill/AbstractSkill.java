package simple.server.extension.d20.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import simple.server.core.entity.RPEntity;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.dice.DiceParser;
import simple.server.extension.d20.dice.DieRoll;
import simple.server.extension.d20.rpclass.D20Class;
import static simple.server.extension.d20.skill.D20Skill.modifiers;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractSkill extends RPEntity implements D20Skill {

    private final List<Class<? extends D20Class>> exclusiveClasses
            = new ArrayList<>();
    private final Map<Class<? extends D20Characteristic>, Integer> requirements
            = new HashMap<>();
    private final Map<Class<? extends D20Characteristic>, Integer> opponentRequirements
            = new HashMap<>();
    private static final Logger LOG
            = Logger.getLogger(AbstractSkill.class.getSimpleName());
    public static final String RANK = "rank";
    public final static String RP_CLASS = "Abstract Skill";

    public AbstractSkill() {
        RPCLASS_NAME = getClass().getSimpleName().replaceAll("_", " ");
        setName(RPCLASS_NAME);
        update();
    }

    public AbstractSkill(RPObject object) {
        super(object);
        RPCLASS_NAME = getClass().getSimpleName().replaceAll("_", " ");
        setName(RPCLASS_NAME);
    }

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
                List<DieRoll> parseRoll = DiceParser.parseRoll(eq);
                for (DieRoll roll : parseRoll) {
                    result += roll.makeRoll().getTotal();
                }
            } else {
                result = Integer.parseInt(eq);
            }
        }
        return result;
    }

    @Override
    public Double getRank() {
        return getDouble(RANK);
    }

    @Override
    public void setRank(Double rank) {
        put(RANK, rank);
    }

    @Override
    public String getCharacteristicName() {
        return RPCLASS_NAME;
    }

    @Override
    public String getShortName() {
        return RPCLASS_NAME;
    }

    @Override
    public List<Class<? extends D20Class>> getExclusiveClasses() {
        return exclusiveClasses;
    }

    @Override
    public Map<Class<? extends D20Characteristic>, Integer> getRequirements() {
        return requirements;
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RP_CLASS)) {
            try {
                RPClass clazz = new RPClass(RP_CLASS);
                clazz.addAttribute(RANK, Definition.Type.STRING);
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
    public void update() {
        super.update();
        if (!has(RANK)) {
            put(RANK, "0.0");
        }
    }

    @Override
    public int levelRequirement() {
        return 0;
    }
    
    @Override
    public Map<Class<? extends D20Characteristic>, Integer> getOpponentRequirements() {
        return opponentRequirements;
    }
}
