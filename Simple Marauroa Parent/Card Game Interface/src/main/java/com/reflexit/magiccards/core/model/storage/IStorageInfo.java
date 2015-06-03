package com.reflexit.magiccards.core.model.storage;

public interface IStorageInfo {

    public static final String DECK_TYPE = "deck";
    public static final String COLLECTION_TYPE = "collection";

    /**
     * Get comment.
     *
     * @return comment
     */
    String getComment();

    /**
     * Get property.
     *
     * @param key property key
     * @return value
     */
    String getProperty(String key);

    /**
     * Get type.
     *
     * @return type
     */
    String getType();

    /**
     * Set comment.
     *
     * @param text comment to set
     */
    void setComment(String text);

    /**
     * Set type.
     *
     * @param string type to set
     */
    void setType(String string);

    /**
     * Set virtual.
     *
     * @param value value
     */
    void setVirtual(boolean value);

    /**
     * Is virtual?
     *
     * @return true if virtual.
     */
    boolean isVirtual();

    /**
     * Get name.
     *
     * @return name
     */
    String getName();
}
