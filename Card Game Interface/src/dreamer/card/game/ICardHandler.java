package dreamer.card.game;

import dreamer.card.game.storage.ICardStore;
import dreamer.card.game.storage.IFilteredCardStore;
import java.util.Properties;

public interface ICardHandler {

    public IFilteredCardStore getDBFilteredStore();

    public IFilteredCardStore getDBFilteredStoreWorkingCopy();

    public IFilteredCardStore getLibraryFilteredStore();

    public ICardStore getLibraryCardStore();

    public IFilteredCardStore getLibraryFilteredStoreWorkingCopy();

    public IFilteredCardStore getCardCollectionFilteredStore(String id);

    public IFilteredCardStore getActiveDeckHandler();

    public ICardStore loadFromXml(String filename);

    public void setActiveDeckHandler(IFilteredCardStore store);

    public int downloadUpdates(String set, Properties options) throws Exception, InterruptedException;

    public void loadInitialIfNot() throws Exception;

    public ICardStore getDBStore();
}
