package com.reflexit.magiccards.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.reflexit.magiccards.core.cache.ICardCache;
import com.reflexit.magiccards.core.model.ICardAttributeFormatter;
import com.reflexit.magiccards.core.model.ICardGame;
import com.reflexit.magiccards.core.model.ICardSet;
import com.reflexit.magiccards.core.model.IGame;
import com.reflexit.magiccards.core.model.IGameCellRendererImageFactory;
import com.reflexit.magiccards.core.model.IGameDataManager;
import com.reflexit.magiccards.core.model.storage.db.DBException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DummyGame implements ICardGame {

    public void init() {

    }

    public Runnable getUpdateRunnable() {
        return null;
    }

    public List<ICardCache> getCardCacheImplementations() {
        return new ArrayList<ICardCache>();
    }

    public IGameDataManager getGameDataManagerImplementation() {
        return null;
    }

    public Image getBackCardIcon() {
        return null;
    }

    public Image getGameIcon() {
        return null;
    }

    public List<ICardAttributeFormatter> getGameCardAttributeFormatterImplementations() {
        return new ArrayList<ICardAttributeFormatter>();
    }

    public List<ICardSet> getGameCardSets() {
        return new ArrayList<ICardSet>();
    }

    public IGameCellRendererImageFactory getCellRendererImageFactory() {
        return null;
    }

    public List<String> getColumns() {
        return new ArrayList<String>();
    }

    public String getName() {
        return "Test Game";
    }

  public IGame getDBGame() throws DBException
  {
    return null;
  }
}
