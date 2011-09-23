/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.sound;

/**
 * is a smelly lazy class just keeping the precalculated dbValues which
 * represent loudness.
 * 
 * volume = 0 -> dbValue -> NegNAN, volume = 100 -> dbValue -> 0
 * 
 * @author astridemma 19.02.2007
 * 
 */
public class DBValues {

    /**
     * Utility Class does not need to be instantiated.
     */
    protected DBValues() {
    }
    /**
     * dbValue[0] is mute and dbValue[100] is loudest.
     */
    private static final float[] DB_VALUES = new float[101];


    static {
        // init our volume -> decibel map
        for (int i = 0; i < 101; i++) {
            double level = ((double) i) / 100;
            DB_VALUES[i] = (float) (Math.log(level) / Math.log(10.0) * 20.0);
        }
    }

    /**
     * calculates a dbValue according to the volume.
     *
     * @param volume
     *            the volume to be calculated
     *            <p>
     *            any value < 0 will be adjusted to 0 percent
     *            <p>
     *            any value > 100 will be adjusted to 100 percent
     *
     * @return the calculated dbValue
     */
    public static float getDBValue(int volume) {
        if (volume < 0) {
            volume = 0;
        }
        if (volume > 100) {
            volume = 100;
        }
        return DB_VALUES[volume];
    }
}
