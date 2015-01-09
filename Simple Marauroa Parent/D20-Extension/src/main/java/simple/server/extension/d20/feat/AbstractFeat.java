package simple.server.extension.d20.feat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import marauroa.common.game.RPObject;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.weapon.D20Weapon;
import simple.server.extension.d20.dice.DieEx;
import simple.server.extension.d20.rpclass.D20Class;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractFeat extends RPObject implements D20Feat {

    protected Map<Class<? extends D20Characteristic>, String> bonus
            = new HashMap<>();
    private List<Class<? extends D20Class>> exclusiveClasses
            = new ArrayList<>();
    private List<Class<? extends D20Feat>> requirements = new ArrayList<>();
    protected boolean multiple = false;
    protected D20Weapon focusWeapon = null;
    protected D20Characteristic focusCharacteristic = null;
    protected int minimumLevel = 0;

    @Override
    public String getCharacteristicName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public String getShortName() {
        return getClass().getSimpleName().replaceAll("_", " ");
    }

    @Override
    public int getBonus(Class<? extends D20Characteristic> st) {
        int result = 0;
        if (bonus.containsKey(st)) {
            String eq = bonus.get(st);
            if (eq.contains("d")) {
                result = new DieEx(eq).roll();
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
    public List<Class<? extends D20Feat>> getRequirements() {
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
        return getMinimumLevel();
    }

    /**
     * @return the minimumLevel
     */
    public int getMinimumLevel() {
        return minimumLevel;
    }
}
