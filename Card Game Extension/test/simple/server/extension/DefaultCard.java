package simple.server.extension;

import java.util.List;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class DefaultCard implements ICard {

    private InstanceContent dynamicContent = new InstanceContent();
    private Lookup myLookup = new AbstractLookup(dynamicContent);

    public DefaultCard() {
        dynamicContent.add(new DefaultType() {});
    }

    @Override
    public List<ImageIcon> getImages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Lookup getLookup() {
        return myLookup;
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
