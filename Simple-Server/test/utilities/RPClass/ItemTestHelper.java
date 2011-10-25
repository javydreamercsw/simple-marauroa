package utilities.RPClass;

import simple.server.core.entity.item.Item;
import simple.server.core.entity.item.StackableItem;

public class ItemTestHelper {

    public static Item createItem() {
        return new Item("item", "itemclass", "subclass", null);
    }

    public static Item createItem(final String name) {
        return new Item(name, "itemclass", "subclass", null);
    }

    public static Item createItem(final String name, final int quantity) {
        final StackableItem item = new StackableItem(name, "itemclass", "subclass", null);
        item.setQuantity(quantity);
        return item;
    }
}
