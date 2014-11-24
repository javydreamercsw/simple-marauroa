package simple.server.extension.feat;

import java.util.HashMap;
import java.util.Map;
import simple.server.extension.D20Characteristic;
import simple.server.extension.DieEx;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractFeat implements D20Feat {

    protected Map<Class<? extends D20Characteristic>, String> 
            bonus = new HashMap<>();

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
}
