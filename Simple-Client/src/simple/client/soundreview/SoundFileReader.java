/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.soundreview;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import marauroa.common.Log4J;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundFileReader {

    /** expected location of the sound definition file (classloader). */
    public static final String STORE_PROPERTYFILE =
            "/games/jwrestling/resources/sound/sound.properties";
    private static Properties soundprops;

    /**
     *
     */
    public SoundFileReader() {
    }

    /**
     *
     */
    public void init() {
        init(STORE_PROPERTYFILE);
    }

    private void init(String propertyfile) {
        soundprops = loadSoundProperties(soundprops, propertyfile);
    }

    /**
     *
     */
    public void initWithXml() {
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

    /**
     * @param prop
     *            the Property Object to load to
     * @param url
     *            the Propertyfile
     * @return Properties with name of the sound files
     */
    public Properties loadSoundProperties(Properties prop, String url) {
        InputStream in1;
        if (prop == null) {
            prop = new Properties();
        }
        try {
            in1 = getClass().getResource(url).openStream();

            prop.load(in1);
            in1.close();
        } catch (Exception e) {
            // logger.error(e, e);
        }
        return prop;
    }

    byte[] getData(String soundname) {
        byte[] data;

        String soundbase = SoundFileReader.soundprops.getProperty("soundbase");
        if (soundbase == null) {
            return null;
        }
        if (!soundbase.endsWith("/")) {
            soundbase = soundbase + "/";
        }
        String filename = soundbase + soundname;
        InputStream in;
        ByteArrayOutputStream bout;
        bout = new ByteArrayOutputStream();
        try {
            in = getResourceStream(filename);
            if (in == null) {
                return null;
            }

            transferData(in, bout, 4096);
            in.close();
        } catch (IOException e) {
            Log4J.getLogger(SoundFileReader.class).error(
                    "could not open soundfile " + filename);
            return null;
        }
        data = bout.toByteArray();

        return data;
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
}
