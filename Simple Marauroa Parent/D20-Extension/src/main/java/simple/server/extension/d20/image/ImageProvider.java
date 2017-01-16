package simple.server.extension.d20.image;

import java.awt.image.BufferedImage;
import simple.server.extension.d20.D20Characteristic;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface ImageProvider {

    /**
     * Get the image for this D20Characteristic.
     *
     * @param c characteristic to get the image for
     * @return Image for the D20Characteristic or null if none.
     */
    BufferedImage getImage(D20Characteristic c);
}
