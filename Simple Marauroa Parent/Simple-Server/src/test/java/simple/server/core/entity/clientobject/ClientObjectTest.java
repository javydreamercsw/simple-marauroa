package simple.server.core.entity.clientobject;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class ClientObjectTest extends AbstractSystemTest {

    @Test
    public void checkAttributes() {
        int i = 1;
        ClientObject co = new ClientObject(new RPObject());
        assertFalse(co.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From item extension
        assertTrue(co.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From entity extension
        assertTrue(co.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From rootRPClass extension
        assertTrue(co.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From client object extension
        assertFalse(co.getRPClass()
                .hasDefinition(Definition.DefinitionClass.ATTRIBUTE,
                        "" + (i++)));//From character object extension
        //Check the fill operation
        new RPObject().fill(co);
    }
}
