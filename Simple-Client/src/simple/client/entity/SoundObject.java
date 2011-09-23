/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.entity;

import simple.common.Rand;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.sound.sampled.DataLine;
import simple.client.soundreview.Sound;
import simple.client.soundreview.SoundMaster;

/**
 * Sound Object
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundObject extends ClientEntity {

    private int volume;
    Map<String, String[]> soundArray;

    private void soundInit() {
        soundArray = Sound.soundArray;
    }

    /**
     * Constructor
     */
    public SoundObject() {
    }

    /**
     * Constructor
     * @param x
     * @param y
     */
    public SoundObject(final int x, final int y) {
        super();
        this.x = x;
        this.y = y;

    }

    /**
     * Constructor
     * @param soundPos
     * @param radius
     */
    public SoundObject(final Point2D soundPos, final int radius) {
        x = soundPos.getX();
        y = soundPos.getY();
        audibleRange = radius;
    }

    @Override
    public Rectangle2D getArea() {
        return null;
    }

    /**
     * Set object location
     * @param position
     */
    public void setLocation(final Point2D position) {
        x = position.getX();
        y = position.getY();
    }

    /**
     * @return returns the name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name -
     *            the new value for name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return returns the volume.
     */
    public int getVolume() {
        return volume;
    }

    /**
     * @param volume -
     *            the new value for volume to set.
     */
    public void setVolume(final int volume) {
        this.volume = volume;
    }

    /**
     * Play Sound
     * @param token
     * @param volBot
     * @param volTop
     * @param chance
     * @return
     */
    public DataLine playSound(String token, int volBot, int volTop, int chance) {
        if (soundArray == null) {
            soundInit();
        }
        if (Rand.rand(100) < chance) {
            if (soundArray.containsKey(token)) {

                SoundMaster.play(
                        soundArray.get(token)[Rand.rand(soundArray.get(token).length)]);

            }
        }
        return null;

    }
}
