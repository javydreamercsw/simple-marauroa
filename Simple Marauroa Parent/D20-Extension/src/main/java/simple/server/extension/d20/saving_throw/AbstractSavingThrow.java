package simple.server.extension.d20.saving_throw;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractSavingThrow implements D20SavingThrow {

    protected int base = 10, misc = 0;

    @Override
    public int getBaseScore() {
        return base;
    }

    @Override
    public void setBaseScore(int score) {
        this.base = score;
    }

    @Override
    public int getMiscMod() {
        return misc;
    }

    @Override
    public void setMiscMod(int score) {
        this.misc = score;
    }

    @Override
    public int getScore() {
        return getBaseScore() + getMiscMod();
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
