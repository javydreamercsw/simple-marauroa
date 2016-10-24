package simple.server.extension.d20.item;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = D20ItemAttribute.class)
public class ItemClass extends AbstractItemAttribute {

    @Override
    public String getCharacteristicName() {
        return "item_class";
    }

    @Override
    public String getShortName() {
        return "class";
    }

    @Override
    public String getDescription() {
        return "Item class (sword/armor/...)";
    }
}
