package dreamer.card.game.storage;

import dreamer.card.game.CardGroup;
import dreamer.card.game.ICardCountable;
import dreamer.card.game.ICardFilter;

public interface IFilteredCardStore<T> extends Iterable<T>, ICardCountable {

    public void update(ICardFilter filter) throws Exception;

    public ICardFilter getFilter();

    public ICardStore getCardStore();

    /**
     * Size of filtered list
     *
     * @return
     */
    public int getSize();

    /**
     * Elements in filtered list
     *
     * @return
     */
    public Object[] getElements();

    /**
     * Returns given element in filtered list
     *
     * @param index
     * @return
     */
    public Object getElement(int index);

    /**
     * return top level cards group if grouping is enabled or null if not
     * enabled
     *
     * @return
     */
    public CardGroup[] getCardGroups();

    public CardGroup getCardGroup(int index);

    public boolean contains(T card);
}