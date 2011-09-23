package simple.client.soundreview;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class Background {

    private static final String INT_SEMOS_BLACKSMITH = "int_semos_blacksmith";
    private static final String ZERO_SEMOS_ROAD_E = "0_semos_road_e";
    private static final String ZERO_SEMOS_CITY = "0_semos_city";
    private static final String ZERO_SEMOS_VILLAGE_W = "0_semos_village_w";
    private LinkedList<Sound> sounds;
    private static final Logger logger = Logger.getLogger(Background.class.getSimpleName());

    /**
     *
     * @param name
     */
    public Background(String name) {
        this.clips = new LinkedList<AudioClip>();
        this.sounds = new LinkedList<Sound>();
        if (INT_SEMOS_BLACKSMITH.equals(name)) {
            initSemosBlacksmith();
        } else if (ZERO_SEMOS_ROAD_E.equals(name)) {
            initSemosRoad();
        } else if (ZERO_SEMOS_CITY.equals(name)) {
            initSemosCity();
        } else if (ZERO_SEMOS_VILLAGE_W.equals(name)) {
            initSemosVillage();
        } else {
            logger.log(Level.WARNING, "No background sound for zone: {0}", name);
        }
    }

    private void initSemosVillage() {
    }

    private void initSemosCity() {
    }

    private void initSemosRoad() {
    }

    private void initSemosBlacksmith() {
        addSound("firesparks-1", 11, 3);
        addSound("forgefire-1", 11, 3, true);
        addSound("forgefire-2", 3, 3, true);
        addSound("forgefire-3", 3, 3, true);
    }

    private void addSound(String string, int i, int j, boolean b) {
        sounds.add(new Sound(string, i, j, b));
    }
    private List<AudioClip> clips;

    /**
     *
     * @param soundFileName
     * @param x
     * @param y
     */
    public void addSound(String soundFileName, int x, int y) {
        sounds.add(new Sound(soundFileName, x, y));
    }

    /**
     *
     */
    public void run() {
        for (Sound sound : sounds) {
            clips.add(sound.play());
        }
    }

    /**
     *
     */
    public void stop() {
        for (AudioClip ac : clips) {
            ac.stop();
        }
    }
}
