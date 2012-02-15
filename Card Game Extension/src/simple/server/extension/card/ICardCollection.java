package simple.server.extension.card;

import simple.server.extension.card.storage.ICardStore;

public interface ICardCollection<T> extends ICardStore<T>, ICardCountable {
}
