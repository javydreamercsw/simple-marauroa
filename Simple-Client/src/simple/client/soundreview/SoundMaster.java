/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.soundreview;


import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import simple.client.WorldObjects;
import simple.client.WorldObjects.WorldListener;

/**
 * Sound Master class
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundMaster implements Runnable, WorldListener {

    private static SoundFileMap sfm;
    private static Cliplistener cliplisten;
    private static boolean isMute;
    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(SoundMaster.class);
    /**
     * Clips playing
     */
    public static ConcurrentHashMap<Object, Line> playingClips =
            new ConcurrentHashMap<Object, Line>();
    boolean playing = true;

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.debug("Interrupted Exception caught");
            }
        }
    }

    /**
     * Initialize Sound Master
     */
    public void init() {
        sfm = new SoundFileMap();
        cliplisten = new Cliplistener();
        WorldObjects.addWorldListener(this);
        logger.debug("SoundMaster inited...");
    }

    /**
     * Play AudioClip
     * @param soundName Clip name
     * @return
     */
    public static AudioClip play(String soundName) {
        return play(soundName, false);
    }

    /**
     * Play audio clip with looping option
     * @param soundName Clip name
     * @param shallLoop loop option
     * @return
     */
    public static AudioClip play(String soundName, boolean shallLoop) {
        logger.debug("Requested to play: " + soundName);
        if (isMute) {
            return null;
        }
        if (soundName == null) {
            return null;
        }

        if (sfm == null) {
            return null;
        }

        byte[] o = sfm.get(soundName);
        if (o == null) {
            return null;
        }
        try {
            AudioClip ac = new AudioClip(AudioSystem.getMixer(null), o, 100);
            Clip cl = ac.openLine();
            if (cl != null) {
                cl.addLineListener(cliplisten);
                playingClips.putIfAbsent(cl, cl);
                if (shallLoop) {
                    cl.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    cl.start();
                }
                return ac;
            }
        } catch (UnsupportedAudioFileException e) {
        } catch (IOException e) {
        } catch (LineUnavailableException e) {
        }
        return null;
    }

    class Cliplistener implements LineListener {
        // dont remove this please astriddemma 12.04.2007

        @Override
        public void update(LineEvent event) {

            // if (event.getType().equals(LineEvent.Type.START)) {
            //
            // }
            // if (event.getType().equals(LineEvent.Type.CLOSE)) {
            // }
            if (event.getType().equals(LineEvent.Type.STOP)) {
                event.getLine().close();

                playingClips.remove(event.getLine());
            }

            // if (event.getType().equals(LineEvent.Type.OPEN)) {
            //
            // }
        }
    }

    @Override
    public void playerMoved() {
    }

    // commented for release
    @Override
    public void zoneEntered(String zoneName) {
        // logger.debug(zoneName);
        // bg = new Background(zoneName);
        // bg.run();
    }

    @Override
    public void zoneLeft(String zoneName) {
        // logger.debug(zoneName);
        // bg.stop();
        // bg=null;
    }

    /**
     * Mute sounds
     * @param on
     */
    public static void setMute(boolean on) {
        if (on) {
            Enumeration<Line> enu = playingClips.elements();
            while (enu.hasMoreElements()) {
                Line lin = enu.nextElement();
                lin.close();
            }
        }

        isMute = on;
    }
}
