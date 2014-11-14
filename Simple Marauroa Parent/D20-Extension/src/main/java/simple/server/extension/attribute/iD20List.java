package simple.server.extension.attribute;

/**
 * This represents lists, like skills, equipment, etc
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */


public interface iD20List extends iD20Characteristic{
  
    /**
     * List size, use -1 for unlimited.
     * @return list size.
     */
    public int getSize();
}
