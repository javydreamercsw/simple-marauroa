package com.reflexit.magiccards.core.model.storage;

public interface IStorageContainer<T> {

    /**
     * Get the storage.
     *
     * @return storage
     */
    IStorage<T> getStorage();
}
