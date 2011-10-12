package simple.server.extension;

import java.util.List;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class DefaultCard2 implements ICard, DefaultType {

    @Override
    public List<ImageIcon> getImages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Lookup getLookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
