package simple.client.api;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface PerceptionListener {

    public void onPerceptionBegin(byte type, int timestamp);

    public void onPerceptionEnd(byte type, int timestamp);
}
