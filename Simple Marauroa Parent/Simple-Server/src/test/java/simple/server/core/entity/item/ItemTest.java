package simple.server.core.entity.item;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class ItemTest extends AbstractSystemTest {

    @Test
    public void checkAttributes() {
        int i = 1;
        Item item = new Item(new RPObject());
        assertTrue(item.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From item extension
        assertTrue(item.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From entity extension
        assertTrue(item.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From rootRPClass extension
        assertFalse(item.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From client object extension
        assertFalse(item.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From character object extension
        //Check the fill operation
        new RPObject().fill(item);
    }
}
