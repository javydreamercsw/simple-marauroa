package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class CardImpl implements ICard, Serializable {

    private String setName;

    public CardImpl() {
    }

    /**
     * Get attribute from database
     *
     * @param icf
     * @return Value of the attribute
     */
    @Override
    public Object getObjectByField(ICardField icf) {
        Map<java.lang.String, java.lang.String> attrs = Lookup.getDefault().lookup(IDataBaseCardStorage.class).getAttributesForCard(this);
        for (Entry<java.lang.String, java.lang.String> entry : attrs.entrySet()) {
            if(entry.getKey().equals(icf.getJavaField().getName())){
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get set name
     *
     * @return
     */
    @Override
    public String getSetName() {
        return setName;
    }

    /**
     * Set set name
     *
     * @param name set name
     */
    @Override
    public void setSetName(String name) {
        this.setName = name;
    }
}
