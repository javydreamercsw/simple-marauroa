package com.reflexit.magiccards.core.model;

public interface ICardSet<ICard> extends Iterable<ICard> {

    public String getName();

    public String getGameName();
}