package simple.server.extension.ability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractAbility implements D20Ability {

    protected int score = 0;

    @Override
    public int getDefaultValue() {
        return 11;
    }

    @Override
    public void setAbilityScore(int score) {
        this.score = score;
    }

    @Override
    public int getAbilityScore() {
        return score;
    }

    @Override
    public int getAbilityModifier() {
        return score % 10;
    }
}
