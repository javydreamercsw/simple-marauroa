package simple.server.extension.card.price;

import java.net.URL;

public interface IPriceProvider extends IStoreUpdator {

    String getName();

    URL getURL();
}
