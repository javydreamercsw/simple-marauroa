package com.reflexit.magiccards.core.model;

import java.util.HashMap;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICardFilter {

    ICardField getGroupField();

    int getLimit();

    CardFilterExpr getRoot();

    AbstractSortOrder getSortOrder();

    boolean isFiltered(Object o);

    boolean isOnlyLastSet();

    void setGroupField(ICardField groupField);

    void setLimit(int limit);

    void setNoSort();

    void setOnlyLastSet(boolean onlyLastSet);

    /**
     * sort field
     *
     * @param sortField
     */
    void setSortField(ICardField sortField, boolean accending);

    void update(HashMap map);
}
