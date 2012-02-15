package simple.server.extension.card.storage;

import java.util.Collection;
import simple.server.extension.card.ICardEventManager;
import simple.server.extension.card.ICardSet;
import simple.server.extension.card.IMergeable;

public interface ICardStore<T> extends ICardSet<T>, IMergeable<T>, ICardEventManager<T>, IStorageContainer<T> {

    public String getName();

    public String getComment();

    public boolean isVirtual();

    public T getCard(int id);

    public Collection<T> getCards(int id);
}