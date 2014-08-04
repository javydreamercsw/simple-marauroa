package com.reflexit.magiccards.core.model.storage;

import com.reflexit.magiccards.core.model.*;
import com.reflexit.magiccards.core.model.Editions.Edition;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that implements IFilteredCardStore, it is only contains filtered
 * filteredList and no phisical media
 *
 * @author Alena
 *
 * @param <T>
 */
public abstract class AbstractFilteredCardStore<T> implements IFilteredCardStore<T> {

    private static final CardGroup[] EMPTY_CARD_GROUP = new CardGroup[0];
    protected Collection filteredList = null;
    protected Map<String, CardGroup> groupsList = new LinkedHashMap<String, CardGroup>();
    protected boolean initialized = false;
    protected ICardFilter filter;

    @Override
    public ICardFilter getFilter() {
        synchronized (this) {
            return filter;
        }
    }

    public void setFilter(ICardFilter filter) {
        synchronized (this) {
            this.filter = filter;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.reflexit.magiccards.core.model.IFilteredCardStore#getSize()
     */
    @Override
    public int getSize() {
        initialize();
        return getFilteredList().size();
    }

    @Override
    public Iterator<T> iterator() {
        return getFilteredList().iterator();
    }

    public T getCard(int index) {
        initialize();
        return doGetCard(index);
    }

    protected synchronized final void initialize() {
        if (this.initialized == false) {
            try {
                doInitialize();
            } catch (Exception e) {
                Logger.getLogger(AbstractFilteredCardStore.class.getSimpleName()).log(Level.SEVERE, null, e);
            } finally {
                this.initialized = true;
            }
        }
    }

    protected void doInitialize() throws Exception {
    }

    protected T doGetCard(int index) {
        Collection<T> l = getFilteredList();
        if (l instanceof List) {
            return ((List<T>) getFilteredList()).get(index);
        } else {
            throw new UnsupportedOperationException(l.getClass() + " is not direct access type");
        }
    }

    /**
     * Count of the cards in the filtered list
     *
     * @return Count
     */
    @Override
    public int getCount() {
        int count = 0;
        synchronized (this) {
            for (T element : this) {
                if (element instanceof ICardCountable) {
                    count += ((ICardCountable) element).getCount();
                } else {
                    count++;
                }
            }
            return count;
        }
    }

    @Override
    public boolean contains(T card) {
        boolean result = false;
        synchronized (this) {
            for (T element : this) {
                if (element.equals(card)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    protected synchronized void addFilteredCard(T card) {
        getFilteredList().add(card);
    }

    protected synchronized void removeFilteredCard(T card) {
        getFilteredList().remove(card);
    }

    @Override
    public Object[] getElements() {
        initialize();
        return getFilteredList().toArray();
    }

    @Override
    public Object getElement(int index) {
        return getCard(index);
    }

    protected void setFilteredList(Collection list) {
        this.filteredList = list;
    }

    protected synchronized Collection<T> getFilteredList() {
        if (this.filteredList == null) {
            this.filteredList = doCreateList();
        }
        return this.filteredList;
    }

    @Override
    public synchronized void update(ICardFilter filter) throws Exception {
        setFilter(filter);
        update();
    }

    public synchronized void update() throws Exception {
        initialize();
        if (filter == null) {
            return;
        }
        for (CardGroup g : groupsList.values()) {
            g.getChildren().clear();
            g.setCount(0);
        }
        setFilteredList(null);
        Collection filterCards = filterCards(this.filter);
        getFilteredList().addAll(filterCards);
    }

    public abstract void filterCardsByGroupField(Collection<ICard> filteredList, ICardFilter filter);

    public Collection<ICard> filterCards(ICardFilter filter) throws Exception {
        initialize();
        Collection<ICard> tempFilteredList;
        if (filter.getSortOrder().isEmpty()) {
            tempFilteredList = new ArrayList<ICard>();
            for (Iterator<ICard> iterator = getCardStore().iterator(); iterator.hasNext();) {
                ICard elem = iterator.next();
                if (!filter.isFiltered(elem)) {
                    tempFilteredList.add(elem);
                }
                if (tempFilteredList.size() >= filter.getLimit()) {
                    break;
                }
            }
        } else {
            Comparator<ICard> comp = getSortComparator(filter);
            tempFilteredList = new TreeSet<ICard>(comp);
            for (Iterator<ICard> iterator = getCardStore().iterator(); iterator.hasNext();) {
                ICard elem = iterator.next();
                if (!filter.isFiltered(elem)) {
                    tempFilteredList.add(elem);
                }
                if (tempFilteredList.size() > filter.getLimit()) {
                    ICard last = (ICard) ((TreeSet) tempFilteredList).last();
                    tempFilteredList.remove(last);
                }
            }
        }
        if (filter.isOnlyLastSet()) {
            tempFilteredList = removeSetDuplicates(tempFilteredList);
        }
        if (filter.getGroupField() != null) {
            filterCardsByGroupField(tempFilteredList, filter);
        }
        return tempFilteredList;
    }

    protected Collection<ICard> removeSetDuplicates(Collection<ICard> filteredList) {
        LinkedHashMap<String, ICard> unique = new LinkedHashMap<String, ICard>();
        for (ICard elem : filteredList) {
            if (elem instanceof ICard) {
                ICard card = (ICard) elem;
                ICard old = unique.get(card.getName());
                if (old == null) {
                    unique.put(card.getName(), card);
                } else {
                    Edition oldE = Editions.getInstance().getEditionByName(old.getSetName());
                    Edition newE = Editions.getInstance().getEditionByName(card.getSetName());
                    if (oldE != null && newE != null && oldE.getReleaseDate() != null && newE.getReleaseDate() != null) {
                        if (oldE.getReleaseDate().before(newE.getReleaseDate())) {
                            unique.put(card.getName(), card);
                        }
                        continue;
                    }
                    if (old.getCardId() < card.getCardId()) {
                        unique.put(card.getName(), card);
                    }
                }
            }
        }
        if (unique.size() > 0) {
            return unique.values();
        }
        return filteredList;
    }

    protected Comparator<ICard> getSortComparator(ICardFilter filter) {
        Comparator<ICard> comp = filter.getSortOrder().getComparator();
        return comp;
    }

    /**
     * @param elem
     * @param cardField
     * @return CardGroup
     */
    public abstract CardGroup findGroupIndex(ICard elem, ICardField cardField);

    protected Collection<T> doCreateList() {
        return new ArrayList<T>();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.reflexit.magiccards.core.model.IFilteredCardStore#getCardGroups()
     */
    @Override
    public CardGroup[] getCardGroups() {
        return groupsList.isEmpty() ? EMPTY_CARD_GROUP
                : groupsList.values().toArray(new CardGroup[groupsList.size()]);
    }

    @Override
    public CardGroup getCardGroup(int index) {
        return getCardGroups()[index];
    }

    protected void reload() {
        initialized = false;
        initialize();
    }

    @Override
    public String toString() {
        return filteredList.toString();
    }
}
