package simple.client;

import marauroa.common.game.RPObject;

public class ObjectFactory {

	private static final ObjectChangeListenerAdapter LISTENER = new ObjectChangeListenerAdapter();
/**
 * decide which listener is to be put to the added object. this is ugly. any ideas welcome (durkham).
 * @param object
 * @param perceptionTobject
 */
	public void onAdded(final RPObject object,
			final PerceptionToObject perceptionTobject) {
//		if ("player".equals(object.getRPClass().getName())) {
//			if (StendhalClient.client.isUser(object)) {
//				perceptionTobject.register(object, new TileController());
//			} else {
//				perceptionTobject.register(object, LISTENER);
//			}
//		} else {
			perceptionTobject.register(object, LISTENER);
//		}
	}

}
