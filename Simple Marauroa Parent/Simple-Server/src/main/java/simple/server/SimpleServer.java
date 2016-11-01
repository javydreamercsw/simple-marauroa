package simple.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.marauroad;
import org.openide.util.Lookup;
import org.python.jline.internal.Configuration;
import simple.server.core.engine.SimpleRPRuleProcessor;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class SimpleServer {

    private static final Logger LOG
            = Logger.getLogger(SimpleServer.class.getSimpleName());
    public static marauroad server;
    private static boolean startCLI = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SimpleServer ss = new SimpleServer();
        if (args.length == 1) {
            File config = new File(args[0]);
            if (config.exists()) {
                Properties conf = new Properties();
                try (FileInputStream in = new FileInputStream(config)) {
                    conf.load(in);
                    LOG.log(Level.INFO, "Loaded local properties from: {0}",
                            config.getName());
                    ss.startServer(conf);
                }
                catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error reading properties from disk!",
                            ex);
                }
            } else {
                LOG.log(Level.WARNING,
                        "Unable to find specified configuration "
                        + "file: {0}. Using default!", config);
                ss.startServer();
            }
        } else {
            ss.startServer();
        }
    }

    protected static void startCLI() {
        if (isStartCLI()) {
            new SimpleServerCLI().start();
        }
    }

    public void stopServer() {
        if (server != null) {
            server.finish();
            server = null;
        }
    }

    public void startServer(Properties conf) {
        server = marauroad.getMarauroa(conf);
        internalInit(conf);
    }

    private void internalInit(Properties conf) {
        IRPRuleProcessor rp = Lookup.getDefault().lookup(IRPRuleProcessor.class);
        if (rp != null && rp instanceof SimpleRPRuleProcessor) {
            SimpleRPRuleProcessor srp = (SimpleRPRuleProcessor) rp;
            if (conf.containsKey("server_name")) {
                srp.setGAMENAME(conf.getProperty("server_name"));
            }
            if (conf.containsKey("server_version")) {
                srp.setVERSION(conf.getProperty("server_version"));
            }
        }
        if (server.init(new String[]{})) {
            try {
                System.out.println("Your Host addr: "
                        + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
                Enumeration<NetworkInterface> n
                        = NetworkInterface.getNetworkInterfaces();
                while (n.hasMoreElements()) {
                    NetworkInterface e = n.nextElement();

                    Enumeration<InetAddress> a = e.getInetAddresses();
                    while (a.hasMoreElements()) {
                        InetAddress addr = a.nextElement();
                        System.out.println("  " + addr.getHostAddress());
                    }
                }
                System.out.println("Running on port: "
                        + conf.getProperty("tcp_port"));
            }
            catch (SocketException | UnknownHostException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            startCLI();
        } else {
            LOG.severe("Initialization failed!");
            System.exit(-1);
        }
    }

    public void startServer() {
        Properties conf = new Properties();
        //Load configuration file
        File config = new File("server.ini");
        if (!config.exists()) {
            //Create minimum required
            try {
                INIGenerator gen = Lookup.getDefault().lookup(INIGenerator.class);
                if (gen != null) {
                    gen.generateDefault();
                } else {
                    throw new IOException("Unable to find default ini generator!");
                }
            }
            catch (IOException ex) {
                LOG.log(Level.SEVERE,
                        "Unable to generate default configuration!",
                        ex);
            }
        }
        if (config.exists()) {
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
        }
        server = marauroad.getMarauroa(conf);
        internalInit(conf);
    }

    /**
     * @return the startCLI
     */
    public static final boolean isStartCLI() {
        return startCLI;
    }

    /**
     * @param startCLI the startCLI to set
     */
    public final void setStartCLI(boolean startCLI) {
        SimpleServer.startCLI = startCLI;
    }
}
