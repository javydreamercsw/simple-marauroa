package simple.server.extension.d20.item;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = D20ItemAttribute.class)
public class ItemSubClass extends AbstractItemAttribute {

    @Override
    public String getCharacteristicName() {
        return "item_subclass";
    }

    @Override
    public String getShortName() {
        return "sub class";
    }

    @Override
    public String getDescription() {
        return "Item subclass (long sword/leather/armor/...)";
    }
}
