package simple.server.core.tool;

import java.io.File;
import java.util.Locale;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import static simple.server.core.entity.Entity.NAME;

/**
 * General use tools.
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class Tool {

    private static final Logger LOGGER = Log4J.getLogger(Tool.class);

    /**
     * Encrypt string with provided key.
     *
     * @param str string to encrypt
     * @param key key to encrypt with
     * @return encrypted string
     */
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

    /**
     * Decrypt string with provided key.
     *
     * @param str string to decrypt
     * @param key key to decrypt with
     * @return decrypted string
     */
    public static String decrypt(final String str, final String key) {
        /**
         * To 'decrypt' the string, simply apply the same technique. Is safe to
         * have this since the client never knows the private key.
         */
        return encrypt(str, key);
    }

    /**
     * Replacing underscores.
     *
     * @param value String to replace
     * @return modified string
     */
    public static String removeUnderscores(final String value) {
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

    /**
     * Change letter to upper case
     *
     * @param value String
     * @param index index to change
     * @return modified string
     */
    public static String changeToUpperCase(final String value,
            final int index) {
        LOGGER.debug("Changing value from " + value + "...");
        String result = value.substring(0, index)
                + value.substring(index, index + 1)
                .toUpperCase(Locale.getDefault())
                + value.substring(index + 1);
        LOGGER.debug("to " + result);
        return result;
    }

    /**
     * Delete folder and contents.
     *
     * @param folder folder to delete
     * @param onlyIfEmpty true to delete only if empty, false otherwise
     */
    public static void deleteFolder(final File folder,
            final boolean onlyIfEmpty) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) { //some JVMs return null for empty dirs
                if (files.length > 0 && !onlyIfEmpty || files.length == 0) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            deleteFolder(f, onlyIfEmpty);
                        } else {
                            f.delete();
                        }
                    }
                    folder.delete();
                }
            }
        }
    }

    private Tool() {
    }
    
    public static String extractName(RPObject obj) {
        return obj.has(NAME) ? obj.get(NAME).replace("_", " ")
                : obj.toString();
    }
}
