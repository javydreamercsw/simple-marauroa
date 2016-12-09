package simple.client.api;

import marauroa.common.net.message.MessageS2CPerception;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ExceptionListener {

    public void onException(Exception exception, MessageS2CPerception perception);
}
