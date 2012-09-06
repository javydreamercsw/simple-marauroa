package simple.server.core.tool;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class Tool {

    private static final Logger LOGGER = Log4J.getLogger(Tool.class);

    public static String encrypt(final String str, final String key) {
        StringBuilder sb = new StringBuilder(str);

        final int lenStr = str.length();
        final int lenKey = key.length();
        // For each character in our string, encrypt it...
        for (int i = 0, j = 0; i < lenStr; i++, j++) {
            if (j >= lenKey) {
                j = 0;  // Wrap 'round to beginning of key string.
                //
                // XOR the chars together. Must cast back to char to avoid compile error.
                //
            }
            sb.setCharAt(i, (char) (str.charAt(i) ^ key.charAt(j)));
        }
        return sb.toString();
    }

    public static String decrypt(final String str, final String key) {
        /**
         * To 'decrypt' the string, simply apply the same technique. Is safe to
         * have this since the client never knows the private key.
         */
        return encrypt(str, key);
    }

    public static String removeUnderscores(String value) {
        String result = value;
        while (result.contains("_")) {
            LOGGER.debug("Changing value from " + value + "...");
            //This will be the index of the character to turn upper case
            int underscoreIndex = value.indexOf('_');
            result = value.replaceFirst("_", "");
            changeToUpperCase(result, underscoreIndex);
            LOGGER.debug("to " + result);
        }
        return result;
    }

    public static String changeToUpperCase(String value, final int index) {
        LOGGER.debug("Changing value from " + value + "...");
        String result = value.substring(0, index) + value.substring(index, index + 1).toUpperCase()
                + value.substring(index + 1);
        LOGGER.debug("to " + result);
        return result;
    }
}
