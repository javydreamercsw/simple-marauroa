package com.reflexit.magiccards.core.model.storage;

import com.reflexit.magiccards.core.model.ICardSet;
import java.util.Collection;

public interface IStorage<T> extends Iterable<T> {

    /**
     * Is auto commit?
     *
     * @return true if true
     */
    public boolean isAutoCommit();

    /**
     * Set auto commit.
     *
     * @param value auto commit value
     */
    public void setAutoCommit(boolean value);

    /**
     * Save syncs memory cached data with physical media (from mem to physical).
     * Save would called automatically after each data editing operation unless
     * autoCommit is off.
     */
    public void save();

    /**
     * Needs to be saved?
     *
     * @return true if true
     */
    public boolean isNeedToBeSaved();

    /**
     * Initiate a save command. It will result is actual save if auto-commit is
     * on. If it is off saving should be postponed.
     */
    public void autoSave();

    /**
     * Load syncs memory cashed data with physical media (from physical to mem).
     * Load would called automatically upon first data access if has not been
     * loaded yet.
     */
    public void load();

    /**
     * Is loaded?
     *
     * @return true if true
     */
    public boolean isLoaded();

    /**
     * Get comment for this storage.
     *
     * @return
     */
    public String getComment();

    /**
     * Is virtual?
     *
     * @return true if true
     */
    public boolean isVirtual();

    /**
     * Add card.
     *
     * @param card card to add
     * @return true if successful
     */
    public boolean add(T card, ICardSet set);

    /**
     * Add list of cards.
     *
     * @param list list of cards
     * @return true if successful
     */
    public boolean addAll(Collection<? extends T> list, ICardSet set);

    /**
     * Remove list of cards.
     *
     * @param list list of cards
     * @return true if successful
     */
    public boolean removeAll(Collection<? extends T> list, ICardSet set);

    /**
     * Remove all cards.
     *
     * @return true if successful
     */
    public boolean removeAll(ICardSet set);

    /**
     * Check if card is contained within a set.
     *
     * @param card card to check
     * @param set Set to look into
     * @return true if successful
     */
    public boolean contains(T card, ICardSet set);
    
    /**
     * Check if card is contained.
     *
     * @param card card to check
     * @return true if successful
     */
    public boolean contains(T card);

    /**
     * Remove card.
     *
     * @param card card to remove
     * @return true if successful
     */
    public boolean remove(T card, ICardSet set);

    /**
     * Size.
     *
     * @return size
     */
    public int size();

    /**
     * Get storage name.
     *
     * @return name
     */
    public String getName();
}
