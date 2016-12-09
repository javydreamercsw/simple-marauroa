package simple.server;

import java.io.File;
import java.io.IOException;

/**
 * Interface to generate ini files.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface INIGenerator {

    /**
     * This generates the default file without interaction with the user.
     *
     * @return generated file.
     * @throws java.io.IOException
     */
    public File generateDefault() throws IOException;

    /**
     * This generates the default file without interaction with the user.
     *
     * @return generated file.
     * @throws java.io.IOException
     */
    public File generateCustom() throws IOException;
}
