package simple.server.extension.d20;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.item.Item;
import simple.server.extension.d20.ability.D20Ability;
import simple.server.extension.d20.item.D20ItemAttribute;
import simple.server.extension.d20.level.D20Level;
import simple.server.extension.d20.list.D20List;
import simple.server.extension.d20.misc.D20Misc;
import simple.server.extension.d20.rpclass.D20Class;
import simple.server.extension.d20.stat.D20Stat;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class D20ExtensionTest extends AbstractSystemTest {

    private static final Logger LOG
            = Logger.getLogger(D20ExtensionTest.class.getName());

    public D20ExtensionTest() {
    }

    /**
     * Test of RPClass Definition.
     */
    @Test
    public void testRPItemDefinition() {
        try {
            LOG.info("Item Definition Test");
            Constructor<?> cons = Item.class.getConstructor(RPObject.class);
            RPEntity test = (RPEntity) cons.newInstance(new RPObject());
            test.update();
            assertTrue(test.instanceOf(RPClass.getRPClass(
                    Item.class.newInstance().getRPClassName())));
            boolean pass = false;
            LOG.log(Level.INFO, "Checking item attributes:");
            for (D20ItemAttribute attr : Lookup.getDefault().lookupAll(D20ItemAttribute.class)) {
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
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of RPClass Definition.
     */
    @Test
    public void testRPCharacterDefinition() {
        try {
            Constructor<?> cons = simple.server.core.entity.character.PlayerCharacter.class
                    .getConstructor(RPObject.class);
            RPEntity test = (RPEntity) cons.newInstance(new RPObject());
            test.update();
            assertTrue(test.instanceOf(RPClass.getRPClass(
                    simple.server.core.entity.character.PlayerCharacter.class
                            .newInstance().getRPClassName())));
            boolean pass = false;
            LOG.log(Level.INFO, "Checking abilities:");
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
            LOG.log(Level.INFO, "Checking stats:");
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
            LOG.log(Level.INFO, "Checking lists:");
            for (D20List attr : Lookup.getDefault().lookupAll(D20List.class)) {
                LOG.log(Level.INFO, "{0}: {1}",
                        new Object[]{attr.getCharacteristicName(),
                            test.getSlot(attr.getCharacteristicName()).getCapacity()});
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
            LOG.log(Level.INFO, "Checking miscellaneous:");
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
            assertTrue(test.has(D20Level.LEVEL));
            assertEquals(0, test.getInt(D20Level.LEVEL));
            assertTrue(test.has(D20Level.MAX));
            assertEquals(0, test.getInt(D20Level.MAX));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of RPClass Definition.
     */
    @Test
    public void testRPClassDefinition() {
        LOG.info("RPClass Definition Test");
        //Check all classes are defined properly
        int count = 0;
        Collection<? extends D20Class> classes
                = Lookup.getDefault().lookupAll(D20Class.class);
        for (D20Class r : classes) {
            try {
                LOG.log(Level.INFO, "Checking class: {0}",
                        r.getClass().getSimpleName());
                //Check class
                //Attributes
                Constructor<?> cons = r.getClass().getConstructor(RPObject.class);
                RPEntity test = (RPEntity) cons.newInstance(new RPObject());
                test.update();
                test.setRPClass(RPClass.getRPClass(((Entity) r).getRPClassName()));
                boolean pass = false;
                LOG.log(Level.INFO, "Checking abilities:");
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
                LOG.log(Level.INFO, "Checking stats:");
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
                LOG.log(Level.INFO, "Checking lists:");
                for (D20List attr : Lookup.getDefault().lookupAll(D20List.class)) {
                    LOG.log(Level.INFO, "{0}: {1}",
                            new Object[]{attr.getCharacteristicName(),
                                test.getSlot(attr.getCharacteristicName()).getCapacity()});
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
    }
}
