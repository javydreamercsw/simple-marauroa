package com.reflexit.magiccards.core.model;

public interface IMergeable<ICard> {

    /**
     * Set flag
     *
     * @param v flag
     */
    public void setMergeOnAdd(boolean v);

    /**
     * Get flag's value
     *
     * @return flag
     */
    public boolean getMergeOnAdd();
}