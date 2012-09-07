package com.reflexit.magiccards.core.model;

import java.io.*;
import java.nio.channels.FileChannel;

public class CardFileUtils {

    public static final String UTF8 = "UTF-8";

    /**
     * Copy file
     *
     * @param in source
     * @param out destination
     * @throws IOException if any IO operation fails
     */
    public static void copyFile(final File in, final File out) 
            throws IOException {
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
     * Save stream on file.
     *
     * @param in source
     * @param out destination
     * @throws IOException if any IO operation fails
     */
    public static void saveStream(final InputStream in, final File out) 
            throws IOException {
        if (out.getAbsoluteFile().getParentFile().mkdirs()) {
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
    }

    /**
     *
     * @param reader
     * @return
     * @throws IOException
     */
    public static String readFileAsString(final BufferedReader reader) 
            throws IOException {
        StringBuilder fileData = new StringBuilder(4096);
        char[] buf = new char[1024 * 4];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            fileData.append(buf, 0, numRead);
        }
        return fileData.toString();
    }

    private CardFileUtils() {
    }
}
