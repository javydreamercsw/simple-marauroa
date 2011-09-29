package simple;

import java.io.File;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class IniFileExistsTest {

    @Test
    public void checkIniExists() {
        File ini = new File("server.ini");
        assertTrue(ini.exists());
    }
}
