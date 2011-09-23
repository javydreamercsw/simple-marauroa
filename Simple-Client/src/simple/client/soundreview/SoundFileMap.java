/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.soundreview;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class SoundFileMap implements Map<String, byte[]>, Nullable {

    private Map<String, byte[]> fileMap = new AbsentFileMap();
    SoundFileReader sfr;

    /**
     *
     */
    public SoundFileMap() {
        sfr = new SoundFileReader();
        sfr.init();
    }

    @Override
    public void clear() {
        fileMap.clear();

    }

    @Override
    public boolean containsKey(Object key) {
        return fileMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return fileMap.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, byte[]>> entrySet() {
        return fileMap.entrySet();

    }

    @Override
    public byte[] get(Object key) {
        byte[] byteArray = fileMap.get(key);
        if (byteArray == null) {

            byteArray = sfr.getData((String) key);
            if (byteArray != null) {
                put((String) key, byteArray);
            }
        }
        return byteArray;
    }

    /**
     *
     * @param key
     * @return
     */
    public Object getValue(Object key){
        return fileMap.get(key);
    }

    @Override
    public boolean isEmpty() {
        return fileMap.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return fileMap.keySet();
    }

    @Override
    public byte[] put(String key, byte[] value) {
        if (value == null) {
            throw new NullPointerException();
        }
        try {
            return fileMap.put(key, value);
        } catch (IllegalStateException e) {
            fileMap = Collections.synchronizedMap(new HashMap<String, byte[]>());
            return fileMap.put(key, value);
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends byte[]> t) {
        try {
            fileMap.putAll(t);
        } catch (IllegalStateException e) {
            fileMap = Collections.synchronizedMap(new HashMap<String, byte[]>());
            fileMap.putAll(t);
        }

    }

    @Override
    public byte[] remove(Object key) {

        return fileMap.remove(key);
    }

    @Override
    public int size() {

        return fileMap.size();
    }

    @Override
    public Collection<byte[]> values() {
        return fileMap.values();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isNull() {

        return (fileMap instanceof AbsentFileMap);
    }
}
