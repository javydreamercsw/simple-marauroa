/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.server.core.entity.client_object;

import marauroa.common.game.Definition;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import simple.server.core.entity.clientobject.ClientObject;
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
                        "" + (i++)));//From root extension
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
