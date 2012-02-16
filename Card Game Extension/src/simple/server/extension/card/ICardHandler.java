package simple.server.extension.card;

import java.util.Properties;
import simple.server.extension.card.storage.ICardStore;
import simple.server.extension.card.storage.IFilteredCardStore;

public interface ICardHandler {

    public IFilteredCardStore getMagicDBFilteredStore();

    public IFilteredCardStore getMagicDBFilteredStoreWorkingCopy();

    public IFilteredCardStore getLibraryFilteredStore();

    public ICardStore getLibraryCardStore();

    public IFilteredCardStore getLibraryFilteredStoreWorkingCopy();

    public IFilteredCardStore getCardCollectionFilteredStore(String id);

    public IFilteredCardStore getActiveDeckHandler();

    public ICardStore loadFromXml(String filename);

    public void setActiveDeckHandler(IFilteredCardStore store);

    public int downloadUpdates(String set, Properties options) throws Exception, InterruptedException;

    public void loadInitialIfNot() throws Exception;

    public ICardStore getMagicDBStore();
}
