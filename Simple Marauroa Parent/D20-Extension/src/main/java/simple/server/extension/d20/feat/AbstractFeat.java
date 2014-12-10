package simple.server.extension.d20.feat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.race.D20Race;
import simple.server.extension.d20.weapon.D20Weapon;
import simple.server.extension.d20.dice.DieEx;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractFeat implements D20Feat {

    protected Map<Class<? extends D20Characteristic>, String> bonus
            = new HashMap<>();
    private List<Class<? extends D20Race>> exclusiveRaces = new ArrayList<>();
    private List<Class<? extends D20Feat>> requirements = new ArrayList<>();
    protected boolean multiple = false;
    protected D20Weapon focusWeapon = null;
    protected D20Characteristic focusCharacteristic = null;

    @Override
    public String getName() {
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
    public List<Class<? extends D20Race>> getExclusiveRaces() {
        return exclusiveRaces;
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
}
