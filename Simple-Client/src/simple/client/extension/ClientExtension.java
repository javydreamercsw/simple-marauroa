package simple.client.extension;

import simple.client.event.listener.RPEventListener;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface ClientExtension extends RPEventListener{

    String getMessage(String name);

    void init();

    boolean perform(String name);
}
