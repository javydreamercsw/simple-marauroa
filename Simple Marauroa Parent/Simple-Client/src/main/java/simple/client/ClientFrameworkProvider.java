package simple.client;

import java.net.SocketException;
import marauroa.client.ClientFramework;
import marauroa.client.net.PerceptionHandler;

public interface ClientFrameworkProvider extends Runnable {

    /**
     * @return the clientManager
     */
    ClientFramework getClientManager();

    /**
     * Connect to server.
     *
     * @param host host URL
     * @param username username
     * @param password password
     * @param user_character character
     * @param port port
     * @param game_name game name
     * @param version game version
     * @throws SocketException if something goes wrong with connection.
     */
    public void connect(String host, String username, String password,
            String user_character, String port,
            String game_name, String version) throws SocketException;

    /**
     * Set the Client manager.
     * @param clientManager client manager to set
     */
    public void setClientManager(ClientFramework clientManager);
    
    /**
     * Set PerceptionHandler.
     * @param handler PerceptionHandler to set.
     */
    public void setPerceptionHandler(PerceptionHandler handler);
    
    /**
     * Get PerceptionHandler.
     */
    public PerceptionHandler getPerceptionHandler();
    
    /**
     * Get character name.
     * @return character name
     */
    public String getCharacter();
}
