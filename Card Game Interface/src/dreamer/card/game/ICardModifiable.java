package dreamer.card.game;

public interface ICardModifiable extends ICard {

    boolean setObjectByField(ICardField field, String value);
}
