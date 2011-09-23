package simple.client.soundreview;

import simple.common.Rand;
import java.util.HashMap;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class Sound {

    /**
     *
     */
    public static HashMap<String, String[]> soundArray;
    private int x;
    private int y;
    private String name;

    // private boolean loop=false;
    Sound(String name, int x, int y) {
        super();
        this.x = x;
        this.y = y;
        this.name = name;
    }

    /**
     *
     * @param name
     * @param x
     * @param y
     * @param shallLoop
     */
    public Sound(String name, int x, int y, boolean shallLoop) {
        this(name, x, y);
    // loop = shallLoop;
    }

    /**
     *
     * @return
     */
    public AudioClip play() {
        if (soundArray.containsKey(name)) {

            return SoundMaster.play(
                    soundArray.get(name)[Rand.rand(soundArray.get(name).length)]);
        } else {
            return SoundMaster.play(name);
        }
    }


    static {
        soundArray = new HashMap<String, String[]>();

        soundArray.put("crowd-mix", new String[]{"crowd-boo.wav",
                    "crowd-cheer.wav"
                });
    }
}
