/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.sound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundEffectMap {

    /**
     * the singleton instance initiated by default.
     */
    private static final SoundEffectMap INSTANCE = new SoundEffectMap();

    /**
     * @return the singleton instance
     */
    static SoundEffectMap getInstance() {
        return INSTANCE;
    }
    /**
     * stores the named sound effects.
     */
    private Map<String, Object> sfxmap = Collections.synchronizedMap(new HashMap<String, Object>(
            256));
    private Map<String, String> pathMap = Collections.synchronizedMap(new HashMap<String, String>(
            256));
    private Map<String, ClipRunner> clipRunnerMap = Collections.synchronizedMap(new HashMap<String, ClipRunner>(
            256));

    Object getByName(String name) {
        return sfxmap.get(name);
    }

    boolean containsKey(String key) {
        return sfxmap.containsKey(key);

    }

    Object put(String key, String value) {
        pathMap.put(key, value);
        return sfxmap.put(key, value);
    }

    Object put(String key, ClipRunner value) {
        clipRunnerMap.put(key, value);

        return sfxmap.put(key, value);

    }

    int size() {
        return sfxmap.size();
    }

    /**
     * Returns a <code>ClipRunner</code> object ready to play a sound of the
     * specified library sound name.
     *
     * @param name
     *            token of library sound
     * @return <code>ClipRunner</code> or <b>null</b> if the sound is
     *         undefined
     */
    ClipRunner getSoundClip(String name) {
        return (ClipRunner) getByName(name);
    }
}
