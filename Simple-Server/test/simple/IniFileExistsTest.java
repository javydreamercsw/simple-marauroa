package simple;

import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.Test;

public class IniFileExistsTest {

    @Test
    public void checkIniExists() {
        File ini = new File("server.ini");
        assertTrue(ini.exists());
    }
}
