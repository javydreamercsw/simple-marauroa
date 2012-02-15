package mtg.card;

import simple.server.extension.card.AbstractSortOrder;
import simple.server.extension.card.ICardField;

@SuppressWarnings("serial")
public class MagicSortOrder extends AbstractSortOrder {

    @Override
    public void setSortField(ICardField sortField, boolean accending) {
        MagicSortOrder sortOrder = this;
        MagicCardComparator elem = new MagicCardComparator(sortField, accending);
        if (sortOrder.contains(elem)) {
            sortOrder.remove(elem);
        }
        while (sortOrder.size() > 5) {
            sortOrder.remove(5);
        }
        sortOrder.push(elem);
    }
}
