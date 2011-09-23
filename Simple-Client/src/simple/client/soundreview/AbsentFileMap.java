/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.soundreview;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * AbsentFilemap implements the NullObject Pattern for SoundFileMap
 * 
 */
public class AbsentFileMap implements Map<String, byte[]>, Nullable {

    @Override
    public void clear() {
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<String, byte[]>> entrySet() {
        return null;
    }

    @Override
    public byte[] get(Object key) {
        return new byte[0];
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public byte[] put(String key, byte[] value) {
        throw new IllegalStateException("not yet created");
    }

    @Override
    public void putAll(Map<? extends String, ? extends byte[]> t) {
        throw new IllegalStateException("not yet created");
    }

    @Override
    public byte[] remove(Object key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Collection<byte[]> values() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isNull() {
        return true;
    }
}

