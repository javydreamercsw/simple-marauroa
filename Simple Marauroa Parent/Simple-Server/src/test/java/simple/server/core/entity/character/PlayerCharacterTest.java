package simple.server.core.entity.character;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class PlayerCharacterTest extends AbstractSystemTest {

    /**
     * Test of generateRPClass method, of class PlayerCharacter.
     */
    @Test
    public void testGenerateRPClass() {
        System.out.println("generateRPClass");
        int i = 1;
        PlayerCharacter character = new PlayerCharacter(new RPObject());
        System.out.println(character);
        assertFalse(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From item extension
        assertTrue(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From entity extension
        assertTrue(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From rootRPClass extension
        assertTrue(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From client object extension
        assertTrue(character.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From character object extension
        //Check the fill operation
        new RPObject().fill(character);
    }

}
