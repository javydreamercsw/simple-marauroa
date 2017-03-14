package simple.client.entity;

/**
 * An entity change listener.
 */
public interface EntityChangeListener {
	/**
	 * An entity was changed.
	 * 
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	void entityChanged(ClientEntity entity, Property property);
}
