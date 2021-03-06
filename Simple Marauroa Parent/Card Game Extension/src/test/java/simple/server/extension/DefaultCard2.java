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
public class DefaultCard2 extends RPCard implements IMarauroaCard, DefaultType2 {

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
    public void setSetName(String set) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSetName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "Default card 2";
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
