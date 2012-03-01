package dreamer.card.game.storage.cache;

import dreamer.card.game.storage.database.persistence.CardSet;

public class CannotDetermineSetAbbriviation extends Exception {

    private static final long serialVersionUID = 5548480990926987096L;

    public CannotDetermineSetAbbriviation(CardSet set) {
        super("Cannot determine set abbreviation for " + set.getName());
    }
}
