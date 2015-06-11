package simple.server.extension.d20;

import simple.server.extension.d20.list.D20List;
import simple.server.extension.d20.stat.D20Stat;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Log4J;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.misc.D20Misc;
import simple.server.extension.d20.rpclass.D20Class;
import simple.server.mock.MockSimpleRPWorld;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class D20ExtensionTest {

    private static final Logger LOG
            = Logger.getLogger(D20ExtensionTest.class.getName());

    public D20ExtensionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Log4J.init();

        MockSimpleRPWorld.get();

        Lookup.getDefault().lookupAll(RPEntityInterface.class).stream()
                .map((entity) -> {
                    LOG.log(Level.FINE, "Registering RPEntity: {0}",
                            entity.getClass().getSimpleName());
                    return entity;
                }).forEach((entity) -> {
                    entity.generateRPClass();
                });
    }

    /**
     * Test of RPClass Definition.
     */
    @Test
    public void testRPClassDefinition() {
        try {
            LOG.info("RPClass Definition Test");
            RPClass entity = new RPClass("Test");
            entity.isA(DummyClass.class.newInstance().getRPClassName());
            //Check all classes are defined properly
            int count = 0;
            Collection<? extends D20Class> classes
                    = Lookup.getDefault().lookupAll(D20Class.class);
            for (D20Class r : classes) {
                try {
                    LOG.log(Level.INFO, "Checking class: {0}",
                            r.getClass().getSimpleName());
                    assertTrue(RPClass.hasRPClass(((Entity) r).getRPClassName()));
                    //Check class
                    //Attributes
                    Constructor<?> cons = r.getClass().getConstructor(RPObject.class);
                    RPEntity test = (RPEntity) cons.newInstance(new RPObject());
                    test.update();
                    test.setRPClass(RPClass.getRPClass(((Entity) r).getRPClassName()));
                    assertTrue(test.instanceOf(RPClass.getRPClass(((Entity) r).getRPClassName())));
                    boolean pass = false;
                    for (D20Ability attr : Lookup.getDefault().lookupAll(D20Ability.class)) {
                        LOG.log(Level.INFO, "{0}: {1}",
                                new Object[]{attr.getCharacteristicName(),
                                    test.get(attr.getCharacteristicName())});
                        assertTrue(test.has(attr.getCharacteristicName()));
                        assertTrue(test.getRPClass()
                                .getDefinition(DefinitionClass.ATTRIBUTE,
                                        attr.getCharacteristicName()) != null);
                        pass = true;
                    }
                    assertTrue(pass);
                    //Stats
                    pass = false;
                    for (D20Stat stat : Lookup.getDefault().lookupAll(D20Stat.class)) {
                        LOG.log(Level.INFO, "{0}: {1}",
                                new Object[]{stat.getCharacteristicName(),
                                    test.get(stat.getCharacteristicName())});
                        assertTrue(test.has(stat.getCharacteristicName()));
                        assertTrue(test.getRPClass()
                                .getDefinition(DefinitionClass.ATTRIBUTE,
                                        stat.getCharacteristicName()) != null);
                        pass = true;
                    }
                    assertTrue(pass);
                    //Other attributes
                    pass = false;
                    for (D20List attr : Lookup.getDefault().lookupAll(D20List.class)) {
                        LOG.log(Level.INFO, "{0}: {1}",
                                new Object[]{attr.getCharacteristicName(),
                                    test.getSlot(attr.getCharacteristicName()).size()});
                        assertTrue(test.hasSlot(attr.getCharacteristicName()));
                        assertTrue(test.getRPClass()
                                .getDefinition(DefinitionClass.RPSLOT,
                                        attr.getCharacteristicName()) != null);
                        assertEquals(attr.getSize(),
                                test.getSlot(attr.getCharacteristicName()).getCapacity());
                        pass = true;
                    }
                    assertTrue(pass);
                    pass = false;
                    for (D20Misc attr : Lookup.getDefault().lookupAll(D20Misc.class)) {
                        LOG.log(Level.INFO, "{0}",
                                new Object[]{attr.getCharacteristicName()});
                        assertTrue(test.has(attr.getCharacteristicName()));
                        assertTrue(test.getRPClass()
                                .getDefinition(DefinitionClass.ATTRIBUTE,
                                        attr.getCharacteristicName()) != null);
                        pass = true;
                    }
                    assertTrue(pass);
                    count++;
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    fail();
                }
            }
            if (count == 0) {
                fail("Found no wrestler classes!");
            }
            assertEquals(classes.size(), count);
        } catch (InstantiationException | IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
