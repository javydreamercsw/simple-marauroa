package com.reflexit.magiccards.core.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Comparator for Cards.
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@SuppressWarnings("serial")
public abstract class AbstractSortOrder extends ArrayList<ICardComparator>
        implements Comparator {

    @Override
    @SuppressWarnings("unchecked")
    public int compare(final Object o1, final Object o2) {
        if (o1 == o2) {
            return 0; // this is only case it is 0
        }
        AbstractSortOrder sortOrder = this;
        int dir = sortOrder.isAccending() ? 1 : -1;
        int d = 0;
        if (o1 instanceof ICard && o2 instanceof ICard) {
            ICard c1 = (ICard) o1;
            ICard c2 = (ICard) o2;
            for (int i = sortOrder.size() - 1; i >= 0; i--) {
                ICardComparator elem = sortOrder.get(i);
                d = elem.compare(o1, o2);
                if (d != 0) {
                    return d;
                }
            }
            if (d == 0 && c1.getCardId() != 0) {
                d = dir * (c1.getCardId() - c2.getCardId());
            }
        }
        if (d != 0) {
            return d;
        }
        return dir * (System.identityHashCode(o1)
                - System.identityHashCode(o2));
    }

    /**
     * Returns the comparator
     *
     * @return instance
     */
    public Comparator getComparator() {
        return this;
    }

    /**
     * Sort based on field.
     *
     * @param sortField Field to sort on.
     * @param ascending Sort ascending?
     */
    public abstract void setSortField(final ICardField sortField,
            final boolean ascending);

    public void push(ICardComparator elem) {
        add(elem);
    }

    /**
     * Has this sort field?
     *
     * @param sortField Sort field to look for
     * @return true if found
     */
    public boolean hasSortField(final ICardField sortField) {
        if (size() == 0) {
            return false;
        }
        for (Iterator<ICardComparator> iterator = iterator();
                iterator.hasNext();) {
            ICardComparator comp = iterator.next();
            if (sortField.equals(comp.getField())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is sort ascending on field?
     *
     * @param sortField sort field to look into
     * @return true if ascending
     */
    public boolean isAscending(final ICardField sortField) {
        if (size() == 0) {
            return true;
        }
        for (Iterator<ICardComparator> iterator = iterator();
                iterator.hasNext();) {
            ICardComparator comp = iterator.next();
            if (sortField.equals(comp.getField())) {
                return comp.isAccending();
            }
        }
        return false; // default to false
    }

    /**
     * Is sort ascending?
     *
     * @return true if ascending
     */
    public boolean isAccending() {
        if (size() == 0) {
            return true;
        }
        ICardComparator elem = peek();
        return elem.isAccending();
    }

    public boolean isTop(ICardField sortField) {
        if (size() == 0) {
            return false;
        }
        ICardComparator elem = peek();
        return elem.getField().equals(sortField);
    }

    private ICardComparator peek() {
        return get(size() - 1);
    }
}
