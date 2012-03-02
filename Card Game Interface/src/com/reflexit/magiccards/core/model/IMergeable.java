package com.reflexit.magiccards.core.model;

public interface IMergeable<ICard> {

    public void setMergeOnAdd(boolean v);

    public boolean getMergeOnAdd();
}