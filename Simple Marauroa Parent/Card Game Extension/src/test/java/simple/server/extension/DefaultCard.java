package simple.server.extension;

import com.reflexit.magiccards.core.model.ICardField;
import java.util.List;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import simple.server.extension.card.IMarauroaCard;
import simple.server.extension.card.RPCard;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class DefaultCard extends RPCard implements IMarauroaCard, DefaultType {

    private InstanceContent dynamicContent = new InstanceContent();
    private Lookup myLookup = new AbstractLookup(dynamicContent);

    /**
     *
     */
    public DefaultCard() {
        dynamicContent.add(new DefaultType() {
            @Override
            public String getName() {
                return "DefaultType";
            }
        });
    }

    @Override
    public List<ImageIcon> getImages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return
     */
    @Override
    public Lookup getLookup() {
        return myLookup;
    }

    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSetName(String set) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSetName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "Default card";
    }

    @Override
    public Object getObjectByField(ICardField field) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getCardId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
