package simple.server.extension.d20.level;

import marauroa.common.game.RPObject;
import simple.server.core.entity.Entity;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public abstract class AbstractLevelEntity extends Entity implements D20Level {

    public AbstractLevelEntity() {
        //Do nothing
    }

    public AbstractLevelEntity(RPObject object) {
        super(object);
    }

    @Override
    public int getLevel() {
        int result = 0;
        if (has(D20Level.LEVEL)) {
            result = getInt(D20Level.LEVEL);
        }
        return result;
    }

    @Override
    public void setLevel(int level) {
        put(D20Level.LEVEL, level);
    }

    @Override
    public int getMaxLevel() {
        int result = 0;
        if (has(D20Level.MAX)) {
            result = getInt(D20Level.MAX);
        }
        return result;
    }

    @Override
    public void setMaxLevel(int max) {
        put(D20Level.MAX, max);
    }
}
