package simple.server.extension;

import com.reflexit.magiccards.core.model.ICardField;
import java.util.List;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import simple.server.extension.card.IMarauroaCard;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class DefaultCard2 implements IMarauroaCard, DefaultType2 {

    private InstanceContent dynamicContent = new InstanceContent();
    private Lookup myLookup = new AbstractLookup(dynamicContent);

    public DefaultCard2() {
        dynamicContent.add(new DefaultType2() {

            @Override
            public String getName() {
                return "DefaultType2";
            }
        });
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

    @Override
    public String getSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getObjectByField(ICardField field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCardId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
