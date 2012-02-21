package dreamer.card.game;

import java.io.*;
import java.nio.channels.FileChannel;

public class CardFileUtils {

    public static final String UTF8 = "UTF-8";

    public static void copyFile(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    /**
     * @param in
     * @param out
     * @throws IOException
     */
    public static void saveStream(InputStream in, File out) throws IOException {
        out.getAbsoluteFile().getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024 * 4];
            int i;
            while ((i = in.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fos.close();
        }
    }

    public static String readFileAsString(BufferedReader reader) throws IOException {
        StringBuilder fileData = new StringBuilder(4096);
        char[] buf = new char[1024 * 4];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            fileData.append(buf, 0, numRead);
        }
        return fileData.toString();
    }
}
