package dreamer.card.game.storage;

import dreamer.card.game.ICardEventManager;
import dreamer.card.game.ICardSet;
import dreamer.card.game.IMergeable;
import java.util.Collection;

public interface ICardStore<T> extends ICardSet<T>, IMergeable<T>, 
        ICardEventManager<T>, IStorageContainer<T> {

    public String getName();

    public String getComment();

    public boolean isVirtual();

    public T getCard(int id);

    public Collection<T> getCards(int id);
}