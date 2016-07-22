package simple.server.extension.d20.item;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = D20ItemAttribute.class)
public class ItemAmount extends AbstractItemAttribute {

    @Override
    public String getCharacteristicName() {
        return "item_amount";
    }

    @Override
    public String getShortName() {
        return "amount";
    }

    @Override
    public String getDescription() {
        return "Item amount. Some items (food) have amount of something\n"
                + "(a bottle, a piece of meat)";
    }
}
