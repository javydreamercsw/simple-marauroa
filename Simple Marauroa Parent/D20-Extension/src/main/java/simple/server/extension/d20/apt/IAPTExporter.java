package simple.server.extension.d20.apt;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface IAPTExporter {

    /**
     * Export data to HTML site.
     *
     * @param root
     * @throws IOException
     */
    void export(File root) throws IOException;
}
