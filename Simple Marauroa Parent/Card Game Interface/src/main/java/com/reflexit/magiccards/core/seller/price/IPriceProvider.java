package com.reflexit.magiccards.core.seller.price;

import java.net.URL;

public interface IPriceProvider extends IStoreUpdater {

    /**
     * Price provider's name.
     *
     * @return name
     */
    String getName();

    /**
     * Price provider's URL
     *
     * @return URL
     */
    URL getURL();
}
