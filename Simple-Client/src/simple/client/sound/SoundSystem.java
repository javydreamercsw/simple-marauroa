/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.sound;

import simple.common.MathHelper;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import simple.client.WorldObjects;
import simple.client.entity.ClientEntity;
import simple.client.entity.User;
import simple.client.soundreview.AudioClip;
import simple.client.soundreview.SoundFileReader;

/**
 * This sound system makes available a library of sounds which can be performed
 * under their library sound names. Sounds can be played as one-time occurrences
 * under various operation modi. (Not all combinations of modi are implemented.)
 * Sound volume can be set globally for all played sounds (system level) and per
 * particular sound performance or sound cycle schedule. The sound system also
 * has a global Mute switch.
 * <p>
 * Operation Modi <br>
 * Sounds can be played GLOBAL or MAP-LOCALIZED (attributed with a map location)
 * <br>
 * Sound can be played SINGULAR or in a CYCLIC pattern (which is governed by
 * some random pattern). <br>
 * Sounds can be played CERTAIN or PROBABLE (definable chances). <br>
 * Furthermore, there are the concepts of AUDIBILITY of map sounds and HEARING
 * RANGE of the player, which are both mutable settings.
 * <p>
 * Nature of Library Sounds <br>
 * Library sounds may be multifold and by this consist of a series of singular
 * sound samples. When such a sound is called to perform, one of the alternative
 * samples is randomly selected to play. The definition file determines the
 * inner structure of library sounds, including possible equalizing volume
 * settings.
 * <p>
 * External Files <br>
 * This sound system requires a definition file and a sound database. The
 * definition file is a properties file located in
 * <code>STORE_PROPERTYFILE</code>. The sound database is a ZIP file
 * containing PCM formatted sound samples stored under their sample names.
 * Library sound names, as used in the interface, are related to sample names in
 * the definition file. The location of the database may be also defined in the
 * definition file under property "soundbase".
 * 
 * @author Jane Hunt
 */
public class SoundSystem implements WorldObjects.WorldListener {

    /** the logger instance. */
    private static final Logger logger = Log4J.getLogger(SoundSystem.class);
    private static SoundSystem singleton;
    /** */
    private final Map<ClientEntity, SoundCycle> cycleMap = Collections.synchronizedMap(new HashMap<ClientEntity, SoundCycle>());
    /** */
    private final ArrayList<AmbientSound> ambientList = new ArrayList<AmbientSound>();
    /** the used mixer. */
    private Mixer mixer;
    /** global volume control. */
    private FloatControl volumeCtrl;
    /** current volume setting. */
    private int volumeSetting = 100;
    private float volumeDelta;
    /** true when mute is enabled. */
    private boolean muteSetting;
    /** true when sound is initialized and operative .*/
    private boolean operative;

    /**
     * plays (?) and registers an ambient sound.
     *
     * @param ambient
     *            the sound to be registered
     */
    void playAmbientSound(AmbientSound ambient) {

        ambient.play();

        synchronized (ambientList) {
            ambientList.add(ambient);
        }
    }

    /**
     * removes the ambient sound from the internal list. It should already be
     * stopped.
     *
     * @param ambient
     *            the ambient sound to be removed
     */
    static void stopAmbientSound(AmbientSound ambient) {
        // TODO: assert the sound is stopped
        SoundSystem sys = get();

        synchronized (sys.ambientList) {
            sys.ambientList.remove(ambient);
        }
    }

    /** Stops and removes all ambient sounds. */
    private void clearAmbientSounds() {
        synchronized (ambientList) {
            List<AmbientSound> list = new ArrayList<AmbientSound>(ambientList);
            for (AmbientSound sound : list) {
                sound.terminate();
            }
        }
    }

    /**
     * Starts cyclic performance of a given library sound, attributed to a
     * specific entity on the map. There can only be one sound cycle for an
     * entity at a given time. If an sound cycle is started while a previous
     * cycle is defined for the entity, the previous cycle is discarded and any
     * ongoing sound performance stopped.
     *
     * @param entity
     *            the game object that makes the sound
     * @param token
     *            the library sound
     * @param period
     *            maximum time period for one sound occurrence
     * @param volBot
     *            bottom volume
     * @param volTop
     *            top volume
     * @param chance
     *            percent chance of performance
     * @return SoundCycle
     */
    public static SoundCycle startSoundCycle(ClientEntity entity, String token,
            int period, int volBot, int volTop, int chance) {
        SoundSystem sys = get();
        SoundCycle cycle;
        SoundCycle c1;

        if (!(sys.isOperative())) {
            return null;
        }

        cycle = null;
        synchronized (sys.cycleMap) {
            try {
                cycle = new SoundCycle(entity, token, period, volBot, volTop,
                        chance);
                cycle.play();

                c1 = sys.cycleMap.get(entity);
                if (c1 != null) {
                    c1.terminate();
                }

                sys.cycleMap.put(entity, cycle);
            } catch (IllegalStateException e) {
                logger.error("*** Undefined sound sample: " + token, e);
            }
        }
        return cycle;
    } // startSoundCycle

    /**
     * Stops execution of the sound cycle for a specific map entity. This will
     * interrupt any ongoing sound performance immediately.
     *
     * @param entity_ID
     *            byte[] identity token of the map entity
     */
    public static void stopSoundCycle(ClientEntity entity_ID) {
        SoundCycle cycle;
        SoundSystem sys;

        sys = get();
        cycle = sys.cycleMap.get(entity_ID);
        if (cycle != null) {
            synchronized (sys.cycleMap) {
                sys.cycleMap.remove(entity_ID);
                cycle.terminate();
            }
        }
    }

    /**
     * Loads a junk of data from the jar soundfile and returns it as a byte
     * array.
     *
     * @param name
     * @return the data in the Zipentry
     * @throws IOException
     */
    private byte[] getData(String name) throws IOException {
        InputStream in;
        ByteArrayOutputStream bout;

        in = getResourceStream(name);
        if (in == null) {
            return null;
        }
        bout = new ByteArrayOutputStream();
        transferData(in, bout, 4096);
        in.close();
        return bout.toByteArray();
    }

    /**
     * Whether the parameter sound is available in this sound system.
     *
     * @param name
     *            token of sound
     * @return true, iif it is available
     */
    boolean contains(String name) {
        return (name != null) && SoundEffectMap.getInstance().containsKey(name);
    }

    /**
     * Obtains a resource input stream. Fetches currently from the main
     * program's classloader.
     *
     * @param name
     * @return InputStream
     * @throws IOException
     */
    public InputStream getResourceStream(String name) throws IOException {
        URL url = getClass().getResource(name);
        if (url == null) {

            return null;
        }
        return url.openStream();
    }

    private void init() {

        String hstr;
        int loaded;
        /**
         * count the amount of file that could not be loaded
         */
        int failedCounted;
        int count;
        int pos;

        int loudness;

        if (!initJavaSound()) {
            logger.error("*** SOUNDSYSTEM JAVA INIT ERROR");
            return;
        }

        try {
            Properties prop;
            // load sound properties
            prop = new Properties();
            loadSoundProperties(prop);

            // get sound library filepath
            String soundBase = prop.getProperty("soundbase", "/games/jwrestling/resources/sound/");

            if (prop.isEmpty()) {
                return;
            }
            Enumeration<Object> maps = prop.keys();
            // read all load-permitted sounds listed in properties
            // from soundfile into cache map
            failedCounted = 0;
            loaded = 0;
            count = 0;

            for (String key = (String) maps.nextElement(); maps.hasMoreElements(); key = (String) maps.nextElement()) {
                byte[] soundData;
                String value;
                String name;
                if (isValidEntry(key, prop.getProperty(key))) {
                    name = key.substring(4);
                    value = prop.getProperty(key);

                    logger.info("- sound definition: " + name + " = " + value);

                    String filename = null;
                    pos = value.indexOf(',');
                    if (pos > -1) {
                        filename = value.substring(0, pos);
                    } else {
                        filename = value;
                    }
                    soundData = getData(soundBase + filename);
                    if (soundData == null) {
                        continue;
                    }

                    // construct sound clip from sample data
                    // (we always do that to verify sound sample format)
                    AudioClip sound;
                    try {
                        // determine equalizing loudness setting
                        loudness = 100;
                        pos = value.lastIndexOf(',');
                        if (pos != -1) {
                            loudness = MathHelper.parseIntDefault(
                                    value.substring(pos + 1), 100);
                        }

                        // investigate sample status
                        int i = name.indexOf('.');
                        if (i != -1) {
                            name = name.substring(0, i);
                        }

                        sound = new AudioClip(mixer, soundData, loudness);
                        count++;
                    } catch (Exception e) {
                        // could not validate sound file content
                        hstr = "*** CORRUPTED SOUND: " + name + "=" + filename;
                        logger.error(hstr, e);
                        failedCounted++;
                        continue;
                    }

                    // store new sound object into soundsystem library map if
                    // opted
                    logger.info("- storing mem-library soundclip: " + name);

                    // stores the clip sound in memory
                    ClipRunner clip = SoundEffectMap.getInstance().getSoundClip(
                            name);
                    if (clip == null) {
                        clip = new ClipRunner(name);
                        SoundEffectMap.getInstance().put(name, clip);
                    }
                    clip.addSample(sound);
                    loaded++;
                }
            } // for

            // report to startup console

            hstr = "jWrestling Soundsystem OK: " + count + " samples approved / " + loaded + " loaded / " + SoundEffectMap.getInstance().size()
                    + " library sounds";
            logger.info(hstr);
            if (failedCounted != 0) {
                hstr = "missing or corrupted sounds: " + failedCounted;
                logger.info(hstr);
            }

            // register listeners
            WorldObjects.addWorldListener(this);
            operative = true;
        } catch (IOException e) {
            hstr = "*** SOUNDSYSTEM LOAD ERROR";
            logger.error(hstr, e);
            return;
        }
    } // init

    //
    // (do not load when ",x" trailing path;
    // always load when "." in
    // name)
    /**
     * A key/value pair is assumed valid if
     * <ul>
     * <li>key starts with "sfx." <b>and </b></li>
     * <li>key does not end with ",x"</li>
     * <li>or value contains a "."</li>
     * </ul>.
     *
     * @param key
     * @param value
     * @return true, if it is valid, false otherwise
     */
    boolean isValidEntry(String key, String value) {
        boolean load;
        int pos1;
        if (key.startsWith("sfx.")) {
            pos1 = value.indexOf(',');
            if (pos1 > -1) {
                load = value.substring(pos1 + 1).charAt(0) != 'x';
            } else {
                load = true;
            }
            load |= value.indexOf('.') != -1;
            return load;
        } else {
            return false;
        }
    }

    /**
     * @param prop
     *            the Property Object to load to
     * @throws IOException
     */
    private void loadSoundProperties(Properties prop) throws IOException {
        InputStream in1;

        in1 = getResourceStream(SoundFileReader.STORE_PROPERTYFILE);
        if (in1 == null) {
            logger.info("Sound properties not found at: " + SoundFileReader.STORE_PROPERTYFILE + " deactivating Soundsystem");
            prop = null;
            this.operative = false;
            return;
        }
        prop.load(in1);
        in1.close();
    }

    /**
     * @return <b>true</b> if javaSound init is successful,
     *         <p>
     *         <b>false</b> otherwise
     */
    private boolean initJavaSound() {
        Info info;
        Info[] mixInfos;
        String hstr;
        mixInfos = AudioSystem.getMixerInfo();
        if ((mixInfos == null) || (mixInfos.length == 0)) {
            logger.error("*** SoundSystem: no sound driver available!");
            return false;
        }

        mixer = AudioSystem.getMixer(null); // mixInfos[4] );
        info = mixer.getMixerInfo();
        hstr = "Sound driver: " + info.getName() + "(" + info.getDescription() + ")";
        logger.info(hstr);

        // try a master volume control
        try {
            volumeCtrl = (FloatControl) mixer.getControl(FloatControl.Type.MASTER_GAIN);
            volumeCtrl.setValue(0f);
        } catch (Exception e) {
            logger.debug("SoundSystem: no master volume controls");
        }

        return true;
    } // initJavaSound

    /**
     * Sets the global Mute switch of this sound system. Does nothing on
     * duplicate call.
     * @param v
     */
    public void setMute(boolean v) {
        if (v == muteSetting) {
            return;
        }

        logger.info("- sound system setting mute = " + (v ? "ON" : "OFF"));
        muteSetting = v;

        synchronized (ambientList) {
            for (AmbientSound ambient : ambientList) {
                if (v) {
                    ambient.stop();
                } else {
                    ambient.play();
                }
            }
        }
    } // setMute

    /**
     * Returns the actual state of the global Mute switch of this sound system.
     *
     * @return <b>true</b> if and only if Mute is ON (silent)
     */
    public boolean isMute() {
        return muteSetting;
    }

    /**
     * Sets a global volume level for all sounds played with this sound system.
     * The volume value ranges between 0 (silent) and 100 (loudest).
     *
     * @param volume
     *            0 .. 100
     */
    public void setVolume(int volume) {
        float dB;
        if (volume < 0) {
            volume = 0;
        } else if (volume > 100) {
            volume = 100;
        }

        dB = DBValues.getDBValue(volume);
        logger.info("- sound system setting volume dB = " + dB + "  (gain " + volume + ")");

        volumeSetting = volume;
        if (volumeCtrl != null) {
            volumeCtrl.setValue(dB);
        } else {
            volumeDelta = dB;

            // update ambient sounds
            synchronized (ambientList) {
                for (AmbientSound amb : ambientList) {
                    amb.updateVolume();
                }
            }
        }
    } // setVolume

    /**
     * Returns the current value of this sound system's voume setting.
     *
     * @return volume ranging 0 (silent) .. 100 (loudest)
     */
    public int getVolume() {
        return volumeSetting;
    }

    /**
     * Whether the sound system has been initialized and is ready to operate.
     *
     * @return true, iff the sound system was initialized
     */
    public boolean isOperative() {
        return operative;
    }

    /**
     * @return the singleton instance of the Jwrestling sound system.
     */
    public static SoundSystem get() {
        if (singleton == null) {
            singleton = new SoundSystem();
        }
        return singleton;
    }

    /**
     * Releases any resources associated with this sound system. The system is
     * rendered inoperative.
     */
    public void exit() {
        clearAmbientSounds();
        logger.info("Sound system exit performed, inactive");
    }

    private SoundSystem() {
        init();
    }

    /**
     * Transfers the contents of the input stream to the output stream until the
     * end of input stream is reached.
     *
     * @param input
     * @param output
     * @param bufferSize
     * @throws java.io.IOException
     */
    static void transferData(InputStream input, OutputStream output,
            int bufferSize) throws java.io.IOException {
        byte[] buffer = new byte[bufferSize];
        int len;

        while ((len = input.read(buffer)) > 0) {
            output.write(buffer, 0, len);
        }
    } // transferData
    // ************* INNER CLASSES ***********************
    private String actualZone = "";

    @Override
    public void zoneEntered(String zone) {
        AmbientSound baseAmb;
        AmbientSound ambient;
        Point2D soundPos;
        String hstr;

        hstr = "-- SoundSys: ZONE ENTERED: " + zone;
        logger.debug(hstr);

        actualZone = zone;

//        if (zone.equals("Lobby")) {
//            // creaking tree and wind
//            baseAmb = AmbientStore.getAmbient("harp-1");
//
//            soundPos = new Point2D.Double(10, 45);
//            ambient = new AmbientSound(baseAmb, "road-ados-tree-1", soundPos,
//                    30, 30);
//            playAmbientSound(ambient);
//
//            soundPos = new Point2D.Double(54, 59);
//            ambient = new AmbientSound(baseAmb, "road-ados-tree-2", soundPos,
//                    100, 50);
//            playAmbientSound(ambient);
//
//            soundPos = new Point2D.Double(65, 31);
//            ambient = new AmbientSound(baseAmb, "road-ados-tree-3", soundPos,
//                    100, 30);
//            playAmbientSound(ambient);
//
//            // beach water
//            baseAmb = AmbientStore.getAmbient("water-beach-1");
//
//            soundPos = new Point2D.Double(32, 46);
//            ambient = new AmbientSound(baseAmb, "road-ados-beachwater-1",
//                    soundPos, 7, 25);
//            playAmbientSound(ambient);
//
//            soundPos = new Point2D.Double(43, 47);
//            ambient = new AmbientSound(baseAmb, "road-ados-beachwater-2",
//                    soundPos, 7, 25);
//            playAmbientSound(ambient);
//
//            soundPos = new Point2D.Double(32, 55);
//            ambient = new AmbientSound(baseAmb, "road-ados-beachwater-3",
//                    soundPos, 12, 35);
//            playAmbientSound(ambient);
//
//            // water at bridge
//            baseAmb = AmbientStore.getAmbient("water-flow-1");
//
//            soundPos = new Point2D.Double(47, 47);
//            ambient = new AmbientSound(baseAmb, "road-ados-bridge-1", soundPos,
//                    3, 50);
//            playAmbientSound(ambient);
//
//            // larks
//            baseAmb = AmbientStore.getAmbient("meadow-larks-1");
//
//            soundPos = new Point2D.Double(15, 15);
//            ambient = new AmbientSound(baseAmb, "road-ados-larks-1", soundPos,
//                    30, 50);
//            playAmbientSound(ambient);
//
//            soundPos = new Point2D.Double(32, 33);
//            ambient = new AmbientSound(baseAmb, "road-ados-larks-2", soundPos,
//                    30, 50);
//            playAmbientSound(ambient);
//
//            // bushbirds
//            baseAmb = AmbientStore.getAmbient("bushbirds-1");
//
//            soundPos = new Point2D.Double(83, 56);
//            ambient = new AmbientSound(baseAmb, "road-ados-bushbirds-1",
//                    soundPos, 20, 80);
//            playAmbientSound(ambient);
//
//            soundPos = new Point2D.Double(118, 57);
//            ambient = new AmbientSound(baseAmb, "road-ados-bushbirds-2",
//                    soundPos, 20, 90);
//            playAmbientSound(ambient);
//
//        }
    }

    /**
     * @return the volume delta
     */
    public float getVolumeDelta() {
        return volumeDelta;
    }

    /*
     * Overridden:
     *
     * @see games.jwrestling.client.WorldObjects.WorldListener#zoneLeft(java.lang.String)
     */
    @Override
    public void zoneLeft(String zone) {
        String hstr = "-- SoundSys: ZONE LEFT: " + zone;
        logger.debug(hstr);
        if (zone.equals(actualZone)) {
            clearAmbientSounds();
        }
    }

    /*
     * Overridden:
     *
     * @see games.jwrestling.client.WorldObjects.WorldListener#playerMoved(games.jwrestling.client.entity.Player)
     */
    @Override
    public void playerMoved() {
        // update ambient sounds about player position
        if (isOperative()) {
            if (!isMute()) {
                if (!User.isNull()) {
                    synchronized (ambientList) {
                        for (AmbientSound a : ambientList) {
                            a.performPlayerMoved();
                        }
                    }
                }
            }
        }

    }
}
