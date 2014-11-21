package simple.server.extension.saving_throw;

import simple.server.extension.D20Characteristic;
import simple.server.extension.ability.D20Ability;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface D20SavingThrow extends D20Characteristic{
    /**
     * Get the ability tied to this throw.
     *
     * @return
     */
    public Class<? extends D20Ability> getAbility();
    
    /**
     * Get base score.
     * @return base score.
     */
    public int getBaseScore();
    
    /**
     * Set base score.
     * @param score new base score
     */
    public void setBaseScore(int score);
    
    /**
     * Get misc modifier.
     * @return misc modifier.
     */
    public int getMiscMod();
    
    /**
     * Set misc modifier.
     * @param score new misc modifier
     */
    public void setMiscMod(int score);
    
    /**
     * Get total score.
     * @return total score.
     */
    public int getScore();
}
