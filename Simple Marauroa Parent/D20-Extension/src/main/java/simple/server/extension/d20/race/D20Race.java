package simple.server.extension.d20.race;

import java.util.List;
import simple.server.extension.d20.D20Characteristic;
import simple.server.extension.d20.rpclass.D20Class;

/**
 * This represents a Race in a D20 game.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface D20Race extends D20Characteristic {

    /**
     * Get favored classes.
     *
     * @return favored classes
     */
    List<Class<? extends D20Class>> getFavoredClasses();

    /**
     * Bonus points per level.
     *
     * @param level
     * @return Bonus points for level
     */
    public int getBonusSkillPoints(int level);

    /**
     * Bonus points per level.
     *
     * @param level
     * @return Bonus points for level
     */
    public int getBonusFeatPoints(int level);
    
    /**
     * Race middle age. At middle age, -1 to Str, Dex, 
     * and Con; +1 to Int, Wis, and Cha.
     * 
     * @return Race's middle age.
     */
    public int getMiddleAge();
    
    /**
     * Race old age. At old age, -2 to Str, Dex, and Con; 
     * +1 to Int, Wis, and Cha.
     * 
     * @return Race's old age.
     */
    public int getOldAge();
    
    /**
     * Race venerable age. At venerable age, -3 to Str, Dex, and Con; 
     * +1 to Int, Wis, and Cha.
     * 
     * @return Race's venerable age.
     */
    public int getVenerableAge();
    
    /**
     * Equation to calculate maximum age.
     * @return Race's maximum age die.
     */
    public String getMaximumAge();
}
