package simple.server.extension.card;

import simple.server.extension.card.model.events.ICardEventListener;

public interface ICardEventManager<T> {
	public void addListener(ICardEventListener lis);

	public void removeListener(ICardEventListener lis);

	/**
	 * card values were updated
	 * @param card
	 */
	public void update(T card);
}