package simple.server.extension;

/**
 * This represents lists, like skills, equipment, etc
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface D20List extends D20Characteristic{
  
    /**
     * List size, use -1 for unlimited.
     * @return list size.
     */
    public int getSize();
}
