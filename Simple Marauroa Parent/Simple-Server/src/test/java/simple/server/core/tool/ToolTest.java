package simple.server.core.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class ToolTest {

    public ToolTest() {
    }

    /**
     * Test of encrypt method, of class Tool.
     */
    @Test
    public void testEncryptDecrypt() {
        System.out.println("encrypt/decrypt");
        String str = "Test";
        String key = UUID.randomUUID().toString();
        String result = Tool.encrypt(str, key);
        assertEquals(str, Tool.decrypt(result, key));
    }

    /**
     * Test of removeUnderscores method, of class Tool.
     */
    @Test
    public void testRemoveUnderscores() {
        System.out.println("removeUnderscores");
        String value = "test_string";
        String expResult = "test string";
        String result = Tool.removeUnderscores(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of changeToUpperCase method, of class Tool.
     */
    @Test
    public void testChangeToUpperCase() {
        System.out.println("changeToUpperCase");
        String value = "abc";
        int index = 1;
        String expResult = "aBc";
        String result = Tool.changeToUpperCase(value, index);
        assertEquals(expResult, result);
    }

    /**
     * Test of deleteFolder method, of class Tool.
     */
    @Test
    public void testDeleteFolder() {
        try {
            System.out.println("deleteFolder");
            Path path = Paths.get("Temp");
            Files.createDirectories(path);
            assertTrue(path.toFile().exists());
            boolean onlyIfEmpty = false;
            Tool.deleteFolder(path.toFile(), onlyIfEmpty);
            assertFalse(path.toFile().exists());
            onlyIfEmpty = true;
            Files.createDirectories(path);
            assertTrue(path.toFile().exists());
            Path p2 = Files.createFile(Paths.get(path.toString(), "temp.tmp"));
            assertTrue(p2.toFile().exists());
            Tool.deleteFolder(path.toFile(), onlyIfEmpty);
            assertTrue(path.toFile().exists());
            Files.deleteIfExists(p2);
            assertFalse(p2.toFile().exists());
            Tool.deleteFolder(path.toFile(), onlyIfEmpty);
            assertFalse(path.toFile().exists());
        } catch (IOException ex) {
            Logger.getLogger(ToolTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of extractName method, of class Tool.
     */
    @Test
    public void testExtractName() {
        System.out.println("extractName");
        RPObject obj = new RPObject();
        String name = UUID.randomUUID().toString();
        Tool.setName(obj, name);
        String result = Tool.extractName(obj);
        assertEquals(name, result);
    }
}
