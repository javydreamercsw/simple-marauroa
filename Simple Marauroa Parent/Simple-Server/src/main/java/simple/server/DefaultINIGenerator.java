package simple.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.crypto.RSAKey;
import marauroa.server.db.adapter.H2DatabaseAdapter;
import org.h2.Driver;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.server.application.db.SimpleDatabase;
import simple.server.core.engine.SimpleRPObjectFactory;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.entity.clientobject.ClientObject;

@ServiceProvider(service = INIGenerator.class)
public class DefaultINIGenerator implements INIGenerator {

    /**
     * Where data is read from.
     */
    private static final BufferedReader IN = new BufferedReader(
            new InputStreamReader(System.in));
    /**
     * The name of the output file.
     */
    static final String FILENAME = "server.ini";
    private static String databaseName;
    private static String databaseHost;
    private static String databasePort;
    private static String databaseUsername;
    private static String databasePassword;
    private static String databaseImplementation;
    private static String databaseSystem;
    private static String persistenceUnitName;
    private static String tcpPort;
    private static String worldImplementation;
    private static String ruleprocessorImplementation;
    private static String turnLength;
    private static String statisticsFilename;
    private static String clientObject;
    private static RSAKey rsakey;
    private static String factory;
    private static String supportEmail;
    private RSAKey key;
    protected Map<String, Object> defaults = new HashMap<>();
    private final static Logger LOG
            = Logger.getLogger(DefaultINIGenerator.class.getName());

    public DefaultINIGenerator() {
        defaults.put("database_adapter",
                H2DatabaseAdapter.class.getCanonicalName());
        defaults.put("tcp_port",
                "" + new Random().nextInt(1000) * 3 + 1000);
        defaults.put("jdbc_url",
                "jdbc:h2:mem:;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=TRUE");
        defaults.put("jdbc_class", Driver.class.getCanonicalName());
        defaults.put("turn_length", "" + 50);
        defaults.put("system_account_name", "system");
        defaults.put("system_password", "system");
        defaults.put("system_email", "system@email.com");
    }

    @Override
    public File generateDefault() throws IOException {
        Properties p = new Properties();
        File ini = new File(FILENAME);
        if (ini.exists()) {
            LOG.warning("File already exists, skipping!");
        } else {
            FileOutputStream out;
            LOG.info("Generating minimal default configuration file...");
            out = new FileOutputStream(ini);
            //Add minimum required
            p.putAll(defaults);
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

    /**
     * reads a String from the input. When no String is chosen the defaultValue
     * is used.
     *
     * @param input the buffered input, usually System.in
     * @param defaultValue if no value is written.
     * @return the string read or default if none was read.
     */
    public String getStringWithDefault(final BufferedReader input,
            final String defaultValue) {
        String ret = "";
        try {
            ret = input.readLine();
        }
        catch (final IOException e) {
            LOG.log(Level.SEVERE, null, e);
            System.exit(1);
        }
        if (ret != null && (ret.length() == 0) && (defaultValue != null)) {
            ret = defaultValue;
        }
        return ret;

    }

    /**
     * reads a String from the input. When no String is chosen the errorMessage
     * is is displayed and the application is terminated.
     *
     * @param input the input stream, usually System.in
     * @param errorMessage the error message to print when failing
     * @return string read from input
     */
    public String getStringWithoutDefault(final BufferedReader input,
            final String errorMessage) {
        String ret = "";
        try {
            ret = input.readLine();
        }
        catch (final IOException e) {
            LOG.log(Level.SEVERE, null, e);
            System.exit(1);
        }

        if ((ret == null) || (ret.length() == 0)) {
            System.out.println(errorMessage);
            System.out.println("Terminating...");
            System.exit(1);
        }
        return ret;
    }

    /**
     * Makes the first letter of the source uppercase.
     *
     * @param source the string
     * @return *T*he string, with first letter is upper case.
     */
    public String uppcaseFirstLetter(final String source) {
        return source.length() > 0 ? Character.toUpperCase(source.charAt(0))
                + source.substring(1) : source;
    }

    private String getDatabaseSystem() {
        String temp = "";
        do {
            System.out.println("Which database system do you want to use? \"h2\" is an integrated database that ");
            System.out.println("works out of the box, \"mysql\" requires a MySQL server. If in doubt, say \"h2\": ");
            temp = getStringWithDefault(IN, "h2").toLowerCase().trim();
        } while (!temp.equals("h2") && !temp.equals("mysql"));
        return temp;
    }

    private String getRSAKeyBits() {
        System.out.print("Write size for the RSA key of the server. Be aware that a key bigger than 1024 could be very long to create [512]: ");
        String keySize = getStringWithDefault(IN, "512");
        return keySize;
    }

    private String getStatisticsFilename() {
        return "./server_stats.xml";
    }

    private String getSupportEmail() {
        return "<support email>";
    }

    private String getTurnLength() {
        return "100";
    }

    private String getRuleProcessorImplementation() {
        return SimpleRPRuleProcessor.class.getCanonicalName();
    }

    private String getWorldImplementation() {
        return SimpleRPWorld.class.getCanonicalName();
    }

    private String getTCPPort() {
        return "32170";
    }

    private String getDatabaseImplementation() {
        return SimpleDatabase.class.getCanonicalName();
    }

    private String getFactoryImplementation() {
        return SimpleRPObjectFactory.class.getCanonicalName();
    }

    private void write(PrintWriter out) {
        out.println("# Generated .ini file for Simple Game at " + new Date());
        out.println("# Database and factory classes. Don't edit.");
        out.println("database_implementation=" + databaseImplementation);
        out.println("factory_implementation=" + factory);
        out.println();
        out.println("# Database information. Edit to match your configuration.");
        if (databaseSystem.equals("mysql")) {
            out.println("jdbc_url=jdbc:mysql://" + databaseHost + ":"
                    + databasePort + "/" + databaseName);
            out.println("jdbc_class=com.mysql.jdbc.Driver");
            out.println("jdbc_user=" + databaseUsername);
            out.println("jdbc_pwd=" + databasePassword);
        } else {
            out.println("database_adapter=" + H2DatabaseAdapter.class.getCanonicalName());
            out.println("jdbc_url=jdbc:h2:~/simple/database/h2db;"
                    + "AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE");
            out.println("jdbc_class=org.h2.Driver");
        }
        out.println();
        out.println("# TCP port simple will use. ");
        out.println("tcp_port=" + tcpPort);
        out.println();
        out.println("# World and RP configuration. Don't edit.");
        out.println("world=" + worldImplementation);
        out.println("ruleprocessor=" + ruleprocessorImplementation);
        out.println("client_object= " + getClientObject());
        out.println();
        out.println("turn_length=" + turnLength);
        out.println();
        out.println("server_typeGame=Simple");
        out.println("server_name=Simple Marauroa server");
        out.println("server_version=1.00");
        out.println("server_contact=http://sourceforge.net/tracker/?atid=945763&group_id=193525&func=browse");
        out.println("server_welcome="+getWelcomeMessage());
        out.println();
        out.println("statistics_filename=" + statisticsFilename);
        out.println();
        rsakey.print(out);
    }

    protected String getDatabasePassword() {
        System.out.print("Write value of the database user password: ");
        final String databasepassword = getStringWithoutDefault(IN,
                "Please enter a database password");
        return databasepassword;
    }

    protected String getDatabaseUsername() {
        System.out.print("Write name of the database user: ");
        final String databaseuser = getStringWithoutDefault(IN,
                "Please enter a database user");
        return databaseuser;
    }

    protected String getDatabaseHost() {
        System.out.print("Write name of the database host [localhost]: ");
        final String databasehost = getStringWithDefault(IN, "localhost");
        return databasehost;
    }

    protected String getDatabaseName() {
        System.out.print("Write name of the database [marauroa]: ");
        final String databasename = getStringWithDefault(IN, "marauroa");
        return databasename;
    }

    protected String getDatabasePort() {
        System.out.print("Write port of the database (i.e. 3306 (mysql), "
                + "5432 (postgres), 1527 (JavaDB) [3306]: ");
        return getStringWithDefault(IN, "3306");
    }

    public String getPersistenceUnitName() {
        System.out.print("Write persistence unit name to be used [SimplePU]: ");
        return getStringWithDefault(IN, "SimplePU");
    }

    private String getClientObjectImplementation() {
        System.out.print("Write Client Object Implementation to be used ["
                + ClientObject.class.getCanonicalName() + "]: ");
        return getStringWithDefault(IN, ClientObject.class.getCanonicalName());
    }

    /**
     * @return the clientObject
     */
    public static String getClientObject() {
        return clientObject;
    }

    /**
     * @param aSupportEmail the supportEmail to set
     */
    public static void setSupportEmail(String aSupportEmail) {
        supportEmail = aSupportEmail;
    }

    @Override
    public File generateCustom() throws IOException {
        File ini = new File(FILENAME);
        /**
         * Write configuration for database
         */
        databaseImplementation = getDatabaseImplementation();
        databaseSystem = getDatabaseSystem();
        if (databaseSystem.equals("mysql")) {
            databaseName = getDatabaseName();
            databaseHost = getDatabaseHost();
            databaseUsername = getDatabaseUsername();
            databasePassword = getDatabasePassword();
            databasePort = getDatabasePort();

            System.out.println("Using \"" + databaseName + "\" as database name\n");
            System.out.println("Using \"" + databaseHost + "\" as database host\n");
            System.out.println("Using \"" + databasePort + "\" as database port\n");
            System.out.println("Using \"" + databaseUsername + "\" as database user\n");
            System.out.println("Using \"" + databasePassword + "\" as database user password\n");
            System.out.println("Using \"" + persistenceUnitName + "\" as persistence unit name\n");
            System.out.println();

            System.out.println("In order to make these options effective please run:");
            System.out.println("# mysql");
            System.out.println("  create database " + databaseName + ";");
            System.out.println("  grant all on " + databaseName + ".* to "
                    + databaseUsername + "@localhost identified by '"
                    + databasePassword + "';");
            System.out.println("  exit");
        } else {
            System.out.println("Using integrated h2 database.");
        }
        
        clientObject = getClientObjectImplementation();
        persistenceUnitName = getPersistenceUnitName();

        tcpPort = getTCPPort();

        worldImplementation = getWorldImplementation();
        ruleprocessorImplementation = getRuleProcessorImplementation();

        turnLength = getTurnLength();

        statisticsFilename = getStatisticsFilename();

        factory = getFactoryImplementation();

        setSupportEmail(getSupportEmail());

        /* The size of the RSA Key in bits, usually 512 */
        final String keySize = getRSAKeyBits();
        System.out.println("Using key of " + keySize + " bits.");
        System.out.println("Please wait while the key is generated.");
        rsakey = RSAKey.generateKey(Integer.valueOf(keySize));
        try (PrintWriter out = new PrintWriter(new FileOutputStream(ini))) {
            write(out);
        }
        return ini;
    }

    public static void main(String[] args) {
        try {
            File ini = Lookup.getDefault().lookup(INIGenerator.class)
                    .generateCustom();
            System.out.println(ini + " has been generated.");
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private String getWelcomeMessage() {
        System.out.print("Write welcome message to be used [Empty]: ");
        return getStringWithDefault(IN, "");
    }
}
