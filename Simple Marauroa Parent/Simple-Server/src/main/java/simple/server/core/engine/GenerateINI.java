package simple.server.core.engine;

import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.crypto.RSAKey;

public class GenerateINI {

    /**
     * Where data is read from.
     */
    private static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));
    /**
     * The name of the output file.
     */
    static final String FILENAME = "server.ini";
    private static String gameName = SimpleRPRuleProcessor.getGAMENAME();
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
    private static String version = SimpleRPRuleProcessor.getVERSION();
    private static String dbEntityManager, clientObject;
    private static RSAKey rsakey;
    private static String factory;
    private static String supportEmail;
    private static final Logger LOG = Logger.getLogger(GenerateINI.class.getSimpleName());

    /**
     * reads a String from the input. When no String is chosen the defaultValue
     * is used.
     *
     * @param input the buffered input, usually System.in
     * @param defaultValue if no value is written.
     * @return the string read or default if none was read.
     */
    public static String getStringWithDefault(final BufferedReader input,
            final String defaultValue) {
        String ret = "";
        try {
            ret = input.readLine();
        } catch (final IOException e) {
            LOG.log(Level.SEVERE, null, e);
            System.exit(1);
        }
        if (ret != null && (ret.length() == 0) && (defaultValue != null)) {
            ret = defaultValue;
        }
        return ret;

    }

    /**
     * reads a String from the input. When no String is choosen the errorMessage
     * is is displayed and the application is terminated.
     *
     * @param input the input stream, usually System.in
     * @param errorMessage the error message to print when failing
     * @return string read from input
     */
    public static String getStringWithoutDefault(final BufferedReader input, final String errorMessage) {
        String ret = "";
        try {
            ret = input.readLine();
        } catch (final IOException e) {
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
    public static String uppcaseFirstLetter(final String source) {
        return source.length() > 0 ? Character.toUpperCase(source.charAt(0)) + source.substring(1) : source;
    }

    private static String getDatabaseSystem() {
        String temp = "";
        do {
            System.out.println("Which database system do you want to use? \"h2\" is an integrated database that ");
            System.out.println("works out of the box, \"mysql\" requires a MySQL server. If in doubt, say \"h2\": ");
            temp = getStringWithDefault(in, "h2").toLowerCase().trim();
        } while (!temp.equals("h2") && !temp.equals("mysql"));
        return temp;
    }

    public static void main(String[] args) throws FileNotFoundException {

        /**
         * Write configuration for database
         */
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
            persistenceUnitName = getPersistenceUnitName();
            dbEntityManager = getDBEntityManager();
            clientObject = getClientObjectImplementation();

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
        final PrintWriter out = new PrintWriter(new FileOutputStream(FILENAME));
        write(out);
        out.close();

        System.out.println(FILENAME + " has been generated.");
    }

    private static String getRSAKeyBits() {
        System.out.print("Write size for the RSA key of the server. Be aware that a key bigger than 1024 could be very long to create [512]: ");
        String keySize = getStringWithDefault(in, "512");
        return keySize;
    }

    private static String getStatisticsFilename() {
        return "./server_stats.xml";
    }

    private static String getSupportEmail() {
        return "<support email>";
    }

    private static String getTurnLength() {
        return "100";
    }

    private static String getRuleProcessorImplementation() {
        return "games.server.core.engine.SimpleRPRuleProcessor";
    }

    private static String getWorldImplementation() {
        return "games.server.core.engine.SimpleRPWorld";
    }

    private static String getTCPPort() {
        return "32170";
    }

    private static String getDatabaseImplementation() {
        return "simple.server.game.db.SimpleDatabase";
    }

    private static String getFactoryImplementation() {
        return "games.server.core.engine.SimpleRPObjectFactory";
    }

    private static void write(PrintWriter out) {
        out.println("# Generated .ini file for Simple Game at " + new Date());
        out.println("# Database and factory classes. Don't edit.");
        out.println("database_implementation=" + databaseImplementation);
        out.println("factory_implementation=" + factory);
        out.println();
        out.println("# Database information. Edit to match your configuration.");
        if (databaseSystem.equals("mysql")) {
            out.println("jdbc_url=jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName);
            out.println("jdbc_class=com.mysql.jdbc.Driver");
            out.println("jdbc_user=" + databaseUsername);
            out.println("jdbc_pwd=" + databasePassword);
        } else {
            out.println("database_adapter=marauroa.server.db.adapter.H2DatabaseAdapter");
            out.println("jdbc_url=jdbc:h2:~/simple/database/h2db;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE");
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
        out.println("server_typeGame=" + gameName);
        out.println("server_name=" + gameName + " Marauroa server");
        out.println("server_version=" + version);
        out.println("server_contact=http://sourceforge.net/tracker/?atid=945763&group_id=193525&func=browse");
        out.println("server_welcome=This release is EXPERIMENTAL.\n "
                + "Remember to keep your password completely secret, "
                + "never tell to another friend, player, or admin.");
        out.println();
        out.println("# Extensions configured on the server. Enable at will.");
        out.println("#server_extension=groovy,http");
        out.println("#groovy=games.server.scripting.SimpleGroovyRunner");
        out.println("#http=games.server.extension.SimpleHttpServer");
        out.println("#http.port=8080");
        out.println();
        out.println("statistics_filename=" + statisticsFilename);
        out.println();
        rsakey.print(out);
    }

    protected static String getDatabasePassword() {
        System.out.print("Write value of the database user password: ");
        final String databasepassword = getStringWithoutDefault(in,
                "Please enter a database password");
        return databasepassword;
    }

    protected static String getDatabaseUsername() {
        System.out.print("Write name of the database user: ");
        final String databaseuser = getStringWithoutDefault(in,
                "Please enter a database user");
        return databaseuser;
    }

    protected static String getDatabaseHost() {
        System.out.print("Write name of the database host [localhost]: ");
        final String databasehost = getStringWithDefault(in, "localhost");
        return databasehost;
    }

    protected static String getDatabaseName() {
        System.out.print("Write name of the database [marauroa]: ");
        final String databasename = getStringWithDefault(in, "marauroa");
        return databasename;
    }

    protected static String getDatabasePort() {
        System.out.print("Write port of the database (i.e. 3306 (mysql), 5432 (postgres), 1527 (JavaDB) [3306]: ");
        return getStringWithDefault(in, "3306");
    }

    public static String getPersistenceUnitName() {
        System.out.print("Write persistence unit name to be used [SimplePU]: ");
        return getStringWithDefault(in, "SimplePU");
    }

    private static String getDBEntityManager() {
        System.out.print("Write Database Entity Manager to be used [simple.server.core.engine.DBEntityManager]: ");
        return getStringWithDefault(in, "simple.server.core.engine.DBEntityManager");
    }

    private static String getClientObjectImplementation() {
        System.out.print("Write Client Object Implementation to be used [simple.server.core.entity.clientobject.ClientObject]: ");
        return getStringWithDefault(in, "simple.server.core.entity.clientobject.ClientObject");
    }

    /**
     * @return the dbEntityManager
     */
    public static String getDbEntityManager() {
        return dbEntityManager;
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
}
