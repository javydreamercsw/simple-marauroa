package com.reflexit.magiccards.core.seller.price;

import java.net.URL;

public interface IPriceProvider extends IStoreUpdater {

    String getName();

    URL getURL();
}
