package simple.server.extension.saving_throw;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractSavingThrow implements D20SavingThrow {

    protected int base = 0, misc = 0;

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
}
