package simple.server.extension.d20.feat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import simple.server.core.entity.Entity;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.dice.DiceParser;
import simple.server.extension.d20.dice.DieRoll;
import simple.server.extension.d20.level.D20Level;
import simple.server.extension.d20.rpclass.D20Class;
import simple.server.extension.d20.weapon.D20Weapon;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractFeat extends Entity implements D20Feat {

    protected Map<Class<? extends D20Characteristic>, String> bonus
            = new HashMap<>();
    private final List<Class<? extends D20Class>> exclusiveClasses
            = new ArrayList<>();
    private final Map<Class<? extends D20Characteristic>, Integer> requirements
            = new HashMap<>();
    private final Map<Class<? extends D20Characteristic>, Integer> opponentRequirements
            = new HashMap<>();
    protected boolean multiple = false;
    protected D20Weapon focusWeapon = null;
    protected D20Characteristic focusCharacteristic = null;
    protected int minimumLevel = 0;
    protected int maximumLevel = 0;
    public final static String RP_CLASS = "Feat";
    private static final Logger LOG
            = Logger.getLogger(AbstractFeat.class.getSimpleName());

    public AbstractFeat() {
        RPCLASS_NAME = getClass().getSimpleName().replaceAll("_", " ");
        setName(RPCLASS_NAME);
    }

    public AbstractFeat(RPObject object) {
        super(object);
        RPCLASS_NAME = getClass().getSimpleName().replaceAll("_", " ");
        setName(RPCLASS_NAME);
        setRPClass(RPCLASS_NAME);
        update();
    }

    public AbstractFeat(int level) {
        RPCLASS_NAME = getClass().getSimpleName().replaceAll("_", " ");
        setName(RPCLASS_NAME);
        put(D20Level.LEVEL, level);
        setRPClass(RPCLASS_NAME);
        update();
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
    public int getBonus(Class<? extends D20Characteristic> st) {
        int result = 0;
        if (bonus.containsKey(st)) {
            String eq = bonus.get(st);
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
    public List<Class<? extends D20Class>> getExclusiveClasses() {
        return exclusiveClasses;
    }

    @Override
    public Map<Class<? extends D20Characteristic>, Integer> getRequirements() {
        return requirements;
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public D20Characteristic getFocusCharacteristic() {
        return focusCharacteristic;
    }

    @Override
    public D20Weapon getFocusWeapon() {
        return focusWeapon;
    }

    @Override
    public Map<Class<? extends D20Characteristic>, String> getBonuses() {
        return bonus;
    }

    @Override
    public int levelRequirement() {
        return minimumLevel;
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RP_CLASS)) {
            try {
                RPClass clazz = new RPClass(RP_CLASS);
                clazz.isA(Entity.class.newInstance().getRPClassName());
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
    public Map<Class<? extends D20Characteristic>, Integer> getOpponentRequirements() {
        return opponentRequirements;
    }

    @Override
    public int getMaxLevel() {
        return maximumLevel;
    }

    @Override
    public void setMaxLevel(int max) {
        maximumLevel = max;
    }
}
