package mtg.card;

import java.util.Collection;
import java.util.Iterator;
import simple.server.extension.card.CardGroup;
import simple.server.extension.card.ICard;
import simple.server.extension.card.ICardField;
import simple.server.extension.card.ICardFilter;
import simple.server.extension.card.storage.AbstractFilteredCardStore;
import simple.server.extension.card.storage.ICardStore;

/**
 * Class that implements IFilteredCardStore, it is only contains filtered
 * filteredList and no physical media
 *
 * @author Alena
 *
 * @param <T>
 */
public class MagicFilteredCardStore<T> extends AbstractFilteredCardStore {

    @Override
    public void filterCardsByGroupField(Collection filteredList, ICardFilter filter) {
        if (groupsList.size() > 0) {
            CardGroup g = (CardGroup) groupsList.values().iterator().next();
            if (g.getFieldIndex() != filter.getGroupField()) {
                groupsList.clear();
            }
        }
        if (filter.getGroupField() == MagicCardField.TYPE) {
            CardGroup buildTypeGroups = CardStoreUtils.getInstance().buildTypeGroups(filteredList);
            for (Object o : buildTypeGroups.getChildren()) {
                if (o instanceof CardGroup) {
                    CardGroup gr = (CardGroup) o;
                    groupsList.put(gr.getName(), gr);
                }
            }
        } else {
            for (Object element : filteredList) {
                IMagicCard elem = (IMagicCard) element;
                CardGroup group = findGroupIndex(elem, filter.getGroupField());
                if (group != null) {
                    group.add(elem);
                }
            }
        }
        for (Iterator iterator = groupsList.values().iterator(); iterator.hasNext();) {
            CardGroup g = (CardGroup) iterator.next();
            g.removeEmptyChildren();
            if (g.getChildren().isEmpty()) {
                iterator.remove();
            }
        }
    }

    /**
     * @param elem
     * @param cardField
     * @return
     */
    @Override
    public CardGroup findGroupIndex(ICard elem, ICardField cardField) {
        String name;
        try {
            IMagicCard mcard = (IMagicCard) elem;
            if (cardField == MagicCardField.COST) {
                name = Colors.getColorName(mcard.getCost());
            } else if (cardField == MagicCardField.CMC) {
                int ccc = mcard.getCmc();
                if (ccc == 0 && mcard.getType().contains("Land")) {
                    name = "Land";
                } else {
                    name = String.valueOf(ccc);
                }
            } else {
                name = String.valueOf(elem.getObjectByField(cardField));
            }
        } catch (Exception e) {
            name = "Unknown";
        }
        CardGroup g = (CardGroup) this.groupsList.get(name);
        if (g == null && name != null) {
            g = new CardGroup(cardField, name);
            this.groupsList.put(name, g);
        }
        return g;
    }

    @Override
    public ICardStore getCardStore() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}