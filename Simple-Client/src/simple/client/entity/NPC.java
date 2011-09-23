/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.entity;

import simple.common.Rand;
import marauroa.common.game.RPObject;
import simple.client.soundreview.SoundMaster;

/**
 * An NPC entity.
 */
public class NPC extends ClientRPEntity {
    //
    // Entity
    //

    /**
     * Initialize this entity for an object.
     *
     * @param object
     *			  The object.
     *
     * @see #release()
     */
    @Override
    public void initialize(final RPObject object) {
        super.initialize(object);

        type = getType();

        if (type.startsWith("npc")) {
            setAudibleRange(3);
            if (name.equals("Diogenes")) {
                moveSounds = new String[2];
                moveSounds[0] = "laugh-1.wav";
                moveSounds[1] = "laugh-2.wav";
            } else if (name.equals("Carmen")) {
                moveSounds = new String[2];
                moveSounds[0] = "giggle-1.wav";
                moveSounds[1] = "giggle-2.wav";

            } else if (name.equals("Nishiya")) {
                moveSounds = new String[3];
                moveSounds[0] = "cough-11.wav";
                moveSounds[1] = "cough-2.wav";
                moveSounds[2] = "cough-3.wav";
            } else if (name.equals("Margaret")) {
                moveSounds = new String[3];
                moveSounds[0] = "hiccup-1.aiff";
                moveSounds[1] = "hiccup-2.wav";
                moveSounds[2] = "hiccup-3.wav";
            } else if (name.equals("Sato")) {
                moveSounds = new String[1];
                moveSounds[0] = "sneeze-1.wav";
            }
        }
    }
    private long soundWait;

    /**
     * When the entity's position changed.
     *
     * @param x
     *			  The new X coordinate.
     * @param y
     *			  The new Y coordinate.
     */
    @Override
    protected void onPosition(final double x, final double y) {
        super.onPosition(x, y);

        if (soundWait < System.currentTimeMillis() && Rand.rand(1000) < 5) {

            if (moveSounds != null) {
                SoundMaster.play(moveSounds[Rand.rand(moveSounds.length)]);
            }
            soundWait = System.currentTimeMillis() + 2000L;
        }
    }
}
