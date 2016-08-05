package simple.server.core.entity.character;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class CharacterTest extends AbstractSystemTest {

    @Test
    public void checkAttributes() {
        int i = 1;
        PlayerCharacter character = new PlayerCharacter(new RPObject());
        assertFalse(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From item extension
        assertTrue(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From entity extension
        assertFalse(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From rootRPClass extension
        assertFalse(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From client object extension
        assertTrue(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From character object extension
        //Check the fill operation
        new RPObject().fill(character);
    }
}
