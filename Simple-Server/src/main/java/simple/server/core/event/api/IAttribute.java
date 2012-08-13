package simple.server.core.event.api;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import marauroa.common.game.Attributes;
import marauroa.common.game.DetailLevel;
import marauroa.common.game.RPClass;
import marauroa.common.net.InputSerializer;
import marauroa.common.net.OutputSerializer;

/**
 * Wraps the Attributes class as an interface
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IAttribute {

    public Object fill(Attributes atrbts);

    public Object clone() throws CloneNotSupportedException;

    public void setRPClass(RPClass rpc);

    public void setRPClass(String string);

    public RPClass getRPClass();

    public boolean instanceOf(RPClass rpc);

    public boolean isEmpty();

    public int size();

    public boolean has(String string);

    public void put(String string, String string1);

    public void add(String string, int i);

    public void put(String string, int i);

    public void put(String string, double d);

    public void put(String string, List<String> list);

    public String get(String string);

    public int getInt(String string);

    public boolean getBool(String string);

    public double getDouble(String string);

    public List<String> getList(String string);

    public String remove(String string);

    public String toAttributeString();

    public Iterator<String> iterator();

    public void writeObject(OutputSerializer os) throws java.io.IOException;

    public void writeObject(OutputSerializer os, DetailLevel dl) throws IOException;

    public void readObject(InputSerializer is) throws IOException;

    public void clearVisible(boolean bln);

    public void resetAddedAndDeletedAttributes();

    public void setAddedAttributes(Attributes atrbts);

    public void setDeletedAttributes(Attributes atrbts);

    public void applyDifferences(Attributes atrbts, Attributes atrbts1);
}
