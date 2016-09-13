package simple.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.server.marauroad;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class SimpleServer {

    private static final Logger LOG
            = Logger.getLogger(SimpleServer.class.getSimpleName());
    public static marauroad server;

    public static void startServer() {
        if (server != null && server.isAlive()) {
            Properties conf = new Properties();
            //Load configuration file
            File config = new File("server.ini");
            //Load from file
            try (FileInputStream in = new FileInputStream(config)) {
                conf.load(in);
                LOG.log(Level.INFO, "Loaded local properties from: {0}",
                        config.getName());
            }
            catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error reading properties from disk!",
                        ex);
            }
            startServer(conf);
        }
    }

    public static void startServer(Properties conf) {
        server = marauroad.getMarauroa(conf);
        startCLI();
    }

    public void stopServer() {
        if (server != null && server.isAlive()) {
            server.finish();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startServer();
    }

    protected static void startCLI() {
        new SimpleServerCLI().start();
    }
}
