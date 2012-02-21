package dreamer.card.game.price;

import java.net.URL;

public interface IPriceProvider extends IStoreUpdator {

    String getName();

    URL getURL();
}
