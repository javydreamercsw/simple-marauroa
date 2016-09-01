package simple.server.core.tool;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import static simple.server.core.entity.Entity.NAME;

/**
 * General use tools.
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class Tool {

    private static final Logger LOG
            = Logger.getLogger(Tool.class.getSimpleName());

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
            LOG.log(Level.FINE, "Changing value from {0}...", value);
            //This will be the index of the character to turn upper case
            int underscoreIndex = value.indexOf('_');
            result = value.replaceFirst("_", "");
            changeToUpperCase(result, underscoreIndex);
            LOG.log(Level.FINE, "to {0}", result);
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
        LOG.log(Level.FINE, "Changing value from {0}...", value);
        String result = value.substring(0, index)
                + value.substring(index, index + 1)
                .toUpperCase(Locale.getDefault())
                + value.substring(index + 1);
        LOG.log(Level.FINE, "to {0}", result);
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

    /**
     * Sort a map by value in ascending order.
     *
     * @param <K> Key
     * @param <V> Comparable value
     * @param map Map to sort
     * @return sorted list
     */
    public static <K, V extends Comparable<? super V>> Map<K, V>
            sortByValue(Map<K, V> map) {
        return sortByValue(map, true);
    }

    /**
     * Sort a map by value in ascending order.
     *
     * @param <K> Key
     * @param <V> Comparable value
     * @param map Map to sort
     * @param asc true for ascending sort, false otherwise.
     * @return sorted list
     */
    public static <K, V extends Comparable<? super V>> Map<K, V>
            sortByValue(Map<K, V> map, boolean asc) {
        List<Map.Entry<K, V>> list
                = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (asc) {
                    return (o1.getValue()).compareTo(o2.getValue());
                } else {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    protected Tool() {
    }

    public static String extractName(RPObject obj) {
        return obj.has(NAME) ? obj.get(NAME).replace("_", " ")
                : obj.toString();
    }

    /**
     * types of Operating Systems
     */
    public enum OSType {
        Windows, MacOS, Linux, Other
    };

    // cached result of OS detection
    protected static OSType detectedOS;

    /**
     * detect the operating system from the os.name System property and cache
     * the result
     *
     * @return - the operating system detected
     */
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name",
                    "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                detectedOS = OSType.MacOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
}
