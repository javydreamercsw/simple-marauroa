package simple.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;
import marauroa.common.crypto.RSAKey;
import marauroa.server.db.adapter.H2DatabaseAdapter;
import org.h2.Driver;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = INIGenerator.class)
public class DefaultINIGenerator implements INIGenerator {

    private RSAKey key;
    private final static Logger LOG
            = Logger.getLogger(DefaultINIGenerator.class.getName());

    @Override
    public File generateDefault() throws IOException {
        Properties p = new Properties();
        File ini = new File("server.ini");
        if (ini.exists()) {
            LOG.warning("File already exists, skipping!");
        } else {
            FileOutputStream out;
            LOG.info("Generating minimal default configuration file...");
            out = new FileOutputStream(ini);
            //Add minimum required
            p.put("database_adapter",
                    H2DatabaseAdapter.class.getCanonicalName());
            p.put("tcp_port",
                    "" + new Random().nextInt(1000) * 3 + 1000);
            p.put("jdbc_url",
                    "jdbc:h2:mem:;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=TRUE");
            p.put("jdbc_class", Driver.class.getCanonicalName());
            p.put("turn_length", "50");
            LOG.info("Generating encryption keys...");
            key = RSAKey.generateKey(512);
            p.put("e", key.getE().toString());
            p.put("d", key.getD().toString());
            p.put("n", key.getN().toString());
            p.store(out, null);
            out.close();
            LOG.info("Done!");
        }
        return ini;
    }

    @Override
    public File generateCustom() throws IOException {
        Properties p = new Properties();
        File ini = new File("server.ini");
        if (ini.exists()) {
            LOG.warning("File already exists, skipping!");
        } else {

        }
        return ini;
    }
}
