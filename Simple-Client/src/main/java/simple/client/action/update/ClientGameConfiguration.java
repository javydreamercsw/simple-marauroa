package simple.client.action.update;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Read the configuration file for the client.
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com> based on code from
 * hendrik
 */
public class ClientGameConfiguration {

    private static ClientGameConfiguration instance;
    private Properties gameConfig;
    private static Class relativeTo;

    private ClientGameConfiguration() {
        // Singleton pattern, hide constructor
        try {
            Properties temp = new Properties();
            InputStream is = relativeTo == null ? ClientGameConfiguration.class.getResourceAsStream("game-default.properties")
                    : relativeTo.getResourceAsStream("game-default.properties");
            temp.load(is);
            is.close();

            gameConfig = new Properties(temp);
            is = relativeTo == null ? ClientGameConfiguration.class.getResourceAsStream("game.properties")
                    : relativeTo.getResourceAsStream("game.properties");
            if (is != null) {
                gameConfig.load(is);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        if (instance == null) {
            instance = new ClientGameConfiguration();
        }
    }

    /**
     * gets a configuration value, in case it is undefined, the default of
     * game-default.properties is returned. If this is undefined, too, the
     * return value is null
     *
     * @param key key
     * @return configured value
     */
    public static String get(String key) {
        init();
        return instance.gameConfig.getProperty(key);
    }

    /**
     * @param relTo the relativeTo to set
     */
    public static void setRelativeTo(Class relTo) {
        relativeTo = relTo;
    }

    public static Class getRelativeTo() {
        return relativeTo;
    }
}
