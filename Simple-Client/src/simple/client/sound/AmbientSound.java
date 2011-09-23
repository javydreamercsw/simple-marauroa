package simple.client.sound;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import simple.client.entity.SoundObject;
import simple.client.entity.User;

/**
 * An ambient sound is a compound sound consisting of any number of loop sounds
 * and cycle sounds. Loop sounds play continuously without interruption, cycle
 * sounds work as described in class SoundCycle. The ambient sound can be played
 * global or fixed to a map location.
 * 
 * @author Jane Hunt
 */
class AmbientSound {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(AmbientSound.class);
    private final List<LoopSoundInfo> loopSounds = new ArrayList<LoopSoundInfo>();
    private List<SoundCycle> cycleList = new ArrayList<SoundCycle>();
    private String name;
    private SoundObject soundObject;
    private Point2D soundPos;
    private float loudnessDB;
    /**
     * true if AmbientSound is playing.
     */
    private boolean isPlaying;

    private float getVolumeDelta() {
        return SoundSystem.get().getVolumeDelta();
    }

    /**
     * The LoopSoundInfo stores information which is required to start the
     * continuously looping sound elements of this ambient sound.
     */
    private static class LoopSoundInfo implements Cloneable {

        /**
         * String representing the LoopSoundInfo.
         */
        private String name;
        /**
         * the loudness.
         */
        private float loudnessDB;
        private int delay;
        private Clip clip;

        /**
         * constructor.
         *
         * @param sound
         *            the sounds name
         * @param volume
         *            the volume 0..100
         * @param delay
         */
        public LoopSoundInfo(final String sound, final int volume,
                final int delay) {
            name = sound;
            loudnessDB = DBValues.getDBValue(volume);
            this.delay = delay;
        }

        /**
         * copies LoopSoundInfo with <code>clip</code> set to <b>null</b>
         * (clip not playing).
         *
         * @return LoopsoundInfo copy
         */
        @Override
        public LoopSoundInfo clone() {
            LoopSoundInfo si;

            try {
                si = (LoopSoundInfo) super.clone();
                si.clip = null;
            } catch (CloneNotSupportedException e) {
                logger.warn("#### bad clone");
                return null;
            }

            return si;
        }

        /**
         * stops the current clip and sets it to null.
         */
        public synchronized void stopClip() {
            if (clip != null) {
                clip.stop();
                clip = null;
            }
        }
    }

    /**
     * Soundstarter is the inner class of Ambientsound that actually starts
     * playing the current clip.
     *
     *
     */
    private class SoundStarter extends Thread {

        private final LoopSoundInfo soundInfo;
        private float correctionDB;

        /**
         * Starts a looping sound.
         *
         * @param loopInfo
         * @param correctionDB
         */
        public SoundStarter(LoopSoundInfo loopInfo, float correctionDB) {
            this.soundInfo = loopInfo;
            this.correctionDB = correctionDB;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            ClipRunner libClip;

            // get the library sound clip
            libClip = SoundEffectMap.getInstance().getSoundClip(soundInfo.name);
            if (libClip == null) {
                throw new IllegalArgumentException("sound unknown: " + soundInfo.name);
            }

            // handle delay phase request on sample start
            if (soundInfo.delay > 0) {
                try {
                    sleep(soundInfo.delay);
                } catch (final InterruptedException e) {
                }
            }

            synchronized (soundInfo) {
                // terminate an existing sound
                soundInfo.stopClip();

                // start playing
                soundInfo.clip = libClip.getAudioClip(
                        SoundSystem.get().getVolume(), loudnessDB + soundInfo.loudnessDB + correctionDB,
                        getVolumeDelta());
                if (soundInfo.clip != null) {
                    soundInfo.clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                isPlaying = true;
            }
        }
    } // SoundStarter

    /**
     * Creates an unlocalized ambient sound (plays everywhere) with the given
     * overall volume setting.
     *
     * @param name
     *            String representing the name of the Sound
     *
     * @param volume
     *            int 0..100 loudness of ambient sound in total
     *
     */
    public AmbientSound(String name, int volume) {
        this(name, null, 0, volume);
    }

    /**
     * Creates a map-localized ambient sound with the given overall volume
     * setting.
     *
     * @param name
     *            ambient sound name
     * @param point
     *            <code>Point2D</code> map location expressed in coordinate
     *            units
     * @param radius
     *            audibility radius of sound object in coordinate units
     * @param volume
     *            int 0..100 loudness of ambient sound in total
     */
    public AmbientSound(String name, Point2D point, int radius, int volume) {
        String hstr;

        if (name == null) {
            throw new NullPointerException();
        }

        if (radius < 0) {
            throw new IllegalArgumentException("r=" + radius);
        }

        this.name = name;
        soundPos = point;
        loudnessDB = DBValues.getDBValue(volume);

        if (soundPos != null) {
            soundObject = new SoundObject();
            soundObject.setLocation(soundPos);
        }

        if (soundObject != null) {
            hstr = "-- created LOC AMBIENT: " + name + " , rad=" + radius + " vol=" + volume;
        } else {
            hstr = "-- created GLOB AMBIENT: " + name + ", vol=" + volume;
        }

        logger.debug(hstr);
    } // constructor

    /**
     * Creates a map-localized ambient sound with the given settings and the
     * sound composition taken from the parameter ambient sound. (The paradigm
     * presets content equivalent to <code>addCycle()</code> and
     * <code>addLoop()</code> calls.)
     *
     * @param sound
     *            <code>AmbientSound</code> as paradigm for sound composition
     * @param name
     *            ambient sound name
     * @param point
     *            <code>Point2D</code> map location expressed in coordinate
     *            units
     * @param radius
     *            audibility radius of sound object in coordinate units
     * @param volume
     *            int 0..100 loudness of ambient sound in total
     */
    public AmbientSound(AmbientSound sound, String name, Point2D point,
            int radius, int volume) {
        this(name, point, radius, volume);

        for (LoopSoundInfo c : sound.loopSounds) {
            loopSounds.add(c.clone());
        }

        for (SoundCycle c : sound.cycleList) {
            SoundCycle cycle = c.clone();
            if (soundObject != null) {
                cycle.entityRef = soundObject;
            } else {
                cycle.entityRef = null;
            }
            cycleList.add(cycle);
        }

        String hstr = "-- content supplied to " + name + ": " + loopSounds.size() + " loops, " + cycleList.size() + " cycles";
        logger.debug(hstr);
    } // constructor

    /**
     * This adds a loop sound to the ambient sound definition.
     *
     * @param sound
     *            library sound name
     * @param volume
     *            relative play volume of the added sound
     * @param delay
     *            milliseconds of start delay for playing the sound
     */
    public void addLoop(String sound, int volume, int delay) {
        SoundSystem sys;
        LoopSoundInfo info;

        sys = SoundSystem.get();
        if (!sys.contains(sound)) {
            logger.error("*** Ambient Sound: missing sound definition (" + sound + ")");
            return;
        }

        info = new LoopSoundInfo(sound, volume, delay);
        loopSounds.add(info);
    } // addLoop

    /**
     * @param token
     * @param period
     * @param volBot
     * @param volTop
     * @param chance
     */
    public void addCycle(final String token, final int period,
            final int volBot, final int volTop, final int chance) {
        try {
            SoundCycle cycle;
            cycle = new SoundCycle(soundObject, token, period, volBot, volTop,
                    chance);
            cycleList.add(cycle);
        } catch (Exception e) {
            //do nothing
        }
    } // addCycle

    private boolean canPlay() {
        return (soundPos == null);
    }

    /**
     * Starts playing this ambient sound with the given player's hearing
     * parameters. This will take required actions, if this ambient sound is not
     * yet playing, to make it audible, global or relative to the player's
     * position depending on this sound's initializer. This does nothing if this
     * sound is already playing. Playing is suppressed if sound position is
     * outside the hearing range of the player.
     */
    protected void play() {
        float fogDB;

        if (isPlaying) {
            return;
        }

        stop();

        // if map-localized
        if (soundPos != null) {
            // adjust to player settings
            if (User.isNull()) {
                return;
            } else {

                // return if sound object is out of range
                if (!canPlay()) {
                    return;
                }
            }
        }

        // create and start loop sounds
        synchronized (loopSounds) {
            fogDB = getPlayerVolume();
            for (LoopSoundInfo info : loopSounds) {
                new SoundStarter(info, fogDB).start();
            }
        }

        // start cycle sounds
        for (SoundCycle c : cycleList) {
            c.play();
        }

        isPlaying = true;
        String hstr = "- playing ambient: " + name;
        logger.debug(hstr);

    } // play

    /** (Temporarily) stops playing this ambient sound. */
    protected void stop() {
        if (!isPlaying) {
            return;
        }

        // terminate loop sounds
        synchronized (loopSounds) {
            for (LoopSoundInfo info : loopSounds) {
                info.stopClip();
            }
        }

        // stop cycle sounds
        for (SoundCycle c : cycleList) {
            c.stopPlaying();
        }

        isPlaying = false;
        String hstr = "- stopped ambient: " + name;
        logger.debug(hstr);

    } // stop

    /** Unrevokably terminates this ambient sound. */
    public void terminate() {
        stop();

        // terminate cycle sounds
        for (SoundCycle c : cycleList) {
            c.terminate();
        }

        // clear internal sound lists
        loopSounds.clear();
        cycleList.clear();

        // remove this object from sound system
        SoundSystem.stopAmbientSound(this);

        String hstr = "- terminated ambient: " + name;
        logger.debug(hstr);

    } // terminate

    /**
     * Returns the sound volume for this ambient sound relative to the current
     * player position (fog correction value). Returns 0.0 if this sound is not
     * map-localized.
     *
     * @return float dB correction of loudness
     */
    private float getPlayerVolume() {
        double distance;
        int fogVolume;

        // if the sound is global (no position)
        if (soundPos == null) {
            return 0;
        }

        // maximum fog if no player infos available
        if ((User.isNull())) {
            return DBValues.getDBValue(0);
        }

        // determine sound volume cutoff due to distance (fog value)
        distance = soundPos.distance(User.get().getX(), User.get().getY());
        fogVolume = (int) Math.max(0, (95 * (distance) / distance + 5));
        return DBValues.getDBValue(fogVolume);

    } // getPlayerVolume

    /**
     * Informs this ambient sound about the actual player's position and hearing
     * parameters. Does nothing if player is <b>null</b> or the sound is not
     * map-localized. (Otherwise this will adjust sound fog loudness.)
     */
    public void performPlayerMoved() {
        // SoundSystem sys;

        // operation control
        // sys = SoundSystem.get();
        if (soundPos == null) {
            return;
        }

        // if not yet playing, start playing
        if (isPlaying) {
            // decide on stopping to play (when sound object has moved out
            // of range)
            if (canPlay()) {
                updateVolume();
            } else {
                stop();
            }
        } else {
            play();
        }
    } // performPlayerPosition

    /**
     * detects player loudness fog value and sets corrected volume to all.
     * running clips
     */
    public void updateVolume() {
        FloatControl volCtrl;
        float fogDB;

        // detect player loudness fog value
        fogDB = getPlayerVolume();

        // set corrected volume to all running clips
        synchronized (loopSounds) {
            for (LoopSoundInfo info : loopSounds) {
                if (info.clip != null) {
                    volCtrl = (FloatControl) info.clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volCtrl.setValue(SoundSystem.get().getVolumeDelta() + loudnessDB + info.loudnessDB + fogDB);
                }
            }
        }
    } // updateVolume
}
