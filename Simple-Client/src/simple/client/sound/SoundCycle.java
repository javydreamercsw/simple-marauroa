package simple.client.sound;

import simple.common.Rand;
import javax.sound.sampled.DataLine;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject.ID;
import simple.client.entity.ClientEntity;
import simple.client.entity.SoundObject;
import simple.client.soundreview.SoundMaster;

/**
 * A sound cycle loops on performing a library sound. After each termination of
 * a sound performance it chooses a timepoint of the next performance at random
 * within the range of the PEROID setting (milliseconds). It can be furthermore
 * subject to probability, and a range for playing volume can be defined.
 * <p>
 * A sound cycle can be GLOBAL or OBJECT-BOUND, depending on whether a game
 * entity has been supplied during creation. Global sounds always play
 * independent from player position.
 */
public class SoundCycle extends Thread implements Cloneable {

    private static final Logger logger = Log4J.getLogger(SoundCycle.class);
    ClientEntity entityRef;
    private String token;
    private int period;
    private int volBot;
    private int volTop;
    private int chance;
    private DataLine dataline;
    private long waitTime;
    private int playMax;
    private boolean executing;
    private boolean stopped;

    /**
     * Creates a sound cycle for a game entity. Depending on whether <code>
     * entity</code>
     * is void, this cycle is global or object-bound.
     *
     * @param entity
     *            the game entity to which this cycle is bound; if <b>null</b>
     *            then a global cycle is created
     * @param token
     *            library sound token
     * @param period
     *            milliseconds of maximum delay time between singular sound
     *            performances
     * @param volBot
     *            relative bottom volume in percent, ranges 0..100
     * @param volTop
     *            relative top volume in percent, ranges 0..100
     * @param chance
     *            percent chance of performance for singular performances
     */
    public SoundCycle(ClientEntity entity, String token, int period, int volBot,
            int volTop, int chance) {
        super("jWrestling.CycleSound." + token);

        ClipRunner clip;

        if (period < 1000) {
            throw new IllegalArgumentException("illegal sound period");
        }
        if ((volBot < 0) || (volBot > 100) || (volTop < 0) || (volTop > 100) || (volTop < volBot)) {
            throw new IllegalArgumentException("bad volume setting");
        }
        clip = SoundEffectMap.getInstance().getSoundClip(token);
        if (clip == null) {
            // TODO: handle soundeffectMap failure
        }

        if (entity != null) {
            this.entityRef = entity;
        }
        this.token = token;
        this.period = period;
        this.volBot = volBot;
        this.volTop = volTop;
        this.chance = chance;

        // calculate period minimum
        playMax = (int) clip.maxPlayLength();

        stopped = true;
    } // constructor

    /**
     * Terminates the soundcycle.
     */
    public void terminate() {
        ClientEntity o;
        ID oid;
        String hstr;

        o = null;
        if (entityRef != null) {
            o = entityRef;
        }

        if (o != null) {
            oid = o.getID();
            hstr = oid == null ? "VOID" : oid.toString();
        } else {
            hstr = "VOID";
        }

        hstr = "  ** terminating cycle sound: " + token + " / entity=" + hstr;
        logger.debug(hstr);

        if (dataline != null) {
            dataline.stop();
            dataline = null;
        }
        executing = false;
    } // terminate

    /**
     * Temporarily ceases to perform sound playing. (May be resumed through
     * method <code>play()</code>.)
     */
    public void stopPlaying() {
        stopped = true;
    }

    /**
     * Starts or resumes playing this sound cycle.
     */
    public void play() {
        String hstr;

        stopped = false;
        if (!isAlive()) {
            hstr = "  ** starting cycle sound: " + token + " / entity=?";
            logger.debug(hstr);

            executing = true;
            try {
                start();
            } catch (OutOfMemoryError e) {
                // If the number of threads is limmited a OutOfMemoryError is
                // thrown.
                // The soundsystem can create a huge amount of threads, so we
                // catch
                // it and simply ignore it.
                logger.debug(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        ClientEntity o;

        while (executing) {
            waitTime = Math.max(playMax, Rand.rand(period));
            try {
                sleep(waitTime);
            } catch (InterruptedException e) {
            }

            if (!executing) {
                return;
            }

            if (stopped) {
                continue;
            }

            // if object bound sound cycle
            if (entityRef != null) {
                o = entityRef;
                if (o != null) {
                    logger.debug("- start cyclic sound for entity: " + o.getType());
                    dataline = ((SoundObject) o).playSound(token, volBot,
                            volTop, chance);
                } else {
                    SoundSystem.stopSoundCycle(entityRef);
                    terminate();
                }
            } else {
                SoundMaster.play(token);
            }
        }
    } // run

    /**
     * Returns a full copy of this SoundCycle, which is not running.
     */
    @Override
    public SoundCycle clone() {
        ClientEntity entity;
        SoundCycle c;

        entity = null;
        if (entityRef != null) {
            entity = entityRef;
        }

        c = new SoundCycle(entity, token, period, volBot, volTop, chance);
        return c;
    }
}
