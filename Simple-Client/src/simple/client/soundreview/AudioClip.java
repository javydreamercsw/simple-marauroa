package simple.client.soundreview;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * This is an audio clip.
 * 
 * @author mtotz
 */
public class AudioClip {

    /** the logger. */
    private static final Logger logger = Log4J.getLogger(AudioClip.class);
    /** the data stream. */
    private byte[] audioData;
    /** length of the clip. */
    private int length;
    /** volume for this clip. */
    private int volume;
    /** the mixer. */
    private Mixer mixer;
    /** need this for a nice toString(). */
    private AudioFormat format;
    /** is the data supported. */
    private boolean supported;
    private Clip line;
    private DataLine.Info info;

    /**
     * creates the audio clip.
     *
     * @param mixer
     *            the Mixer instance to be used
     * @param audioData
     *            the audio data
     * @param volume
     *            the loudness 0..100
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public AudioClip(Mixer mixer, byte[] audioData, int volume)
            throws UnsupportedAudioFileException, IOException {
        this.volume = volume;
        this.mixer = mixer;

        AudioInputStream audioInputStream;
        audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(
                audioData));

        this.audioData = audioData;
        format = audioInputStream.getFormat();

        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            AudioFormat newFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    16,
                    format.getChannels(),
                    format.getChannels() * 2,
                    format.getSampleRate(),
                    false);
            logger.debug("Converting audio format to " + newFormat);
            AudioInputStream newStream = AudioSystem.getAudioInputStream(newFormat, audioInputStream);
            format = newFormat;
            audioInputStream = newStream;
        }
        info = new DataLine.Info(Clip.class, format);
        if (!mixer.isLineSupported(info)) {
            logger.error("format is not supported(" + audioInputStream.getFormat() + ")");
            supported = false;
            return;
        }

        supported = true;

        float frameRate = audioInputStream.getFormat().getFrameRate();
        long frames = audioInputStream.getFrameLength();

        if ((frameRate != AudioSystem.NOT_SPECIFIED) && (frames != AudioSystem.NOT_SPECIFIED)) {
            length = (int) (frames / frameRate * 1000);
        } else {
            length = 0;
        }

    }

    /**
     * @return Returns the length.
     */
    public int getLength() {
        return length;
    }

    /**
     * @return Returns the volume.
     */
    public int getVolume() {
        return volume;
    }

    /**
     * opens the given line with the encapsulated audio data.
     * @return the supported open line or null
     *
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public Clip openLine() throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        if (supported) {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(
                    audioData));

            info = new DataLine.Info(Clip.class,
                    audioInputStream.getFormat());
            if (!mixer.isLineSupported(info)) {
                return null;
            }
            line = (Clip) mixer.getLine(info);
            try {
                line.open(audioInputStream);
                return line;
            } catch (LineUnavailableException e) {
                logger.debug("audioclip cannot be played, no free lines available");
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + (!supported ? "(format not supported by " + mixer.getMixerInfo().getDescription() + ") " : "") + format;
    }

    /**
     *
     */
    public void stop() {
        line.close();
    }
}
