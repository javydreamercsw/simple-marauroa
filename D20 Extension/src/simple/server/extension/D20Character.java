package simple.server.extension;

import marauroa.common.game.RPObject;
import pcgen.core.PlayerCharacter;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class D20Character{

    private PlayerCharacter character = new PlayerCharacter(false);

    public D20Character(RPObject object) {
        character.setAge(15);
    }

    /**
     * @return the character
     */
    public PlayerCharacter getCharacter() {
        return character;
    }
}
