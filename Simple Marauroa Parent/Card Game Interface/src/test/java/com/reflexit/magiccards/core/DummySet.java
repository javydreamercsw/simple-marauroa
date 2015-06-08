package com.reflexit.magiccards.core;

import com.reflexit.magiccards.core.model.ICardGame;
import com.reflexit.magiccards.core.model.ICardSet;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DummySet implements ICardSet {
    private final String name;

    public DummySet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ICardGame getCardGame() {
        return new DummyGame();
    }

    public Collection getCards() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Iterator iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
