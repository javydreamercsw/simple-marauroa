package simple.server.extension.card;

public interface ICardModifiable extends ICard {

    boolean setObjectByField(ICardField field, String value);
}
