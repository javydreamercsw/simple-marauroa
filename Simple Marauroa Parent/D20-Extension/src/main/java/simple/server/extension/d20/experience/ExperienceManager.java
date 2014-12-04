package simple.server.extension.d20.experience;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface ExperienceManager {
    /**
     * Calculate the experience needed for a level.
     * @param level
     * @return experience
     */
    long getExpForLevel(int level);
    
    /**
     * Calculate level based on experience.
     * @param exp
     * @return level
     */
    double getLevelForExperience(long exp);
}
