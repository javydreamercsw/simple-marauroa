/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "card_collection_has_card")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardCollectionHasCard.findAll", query = "SELECT c FROM CardCollectionHasCard c"),
    @NamedQuery(name = "CardCollectionHasCard.findByCardCollectionId", query = "SELECT c FROM CardCollectionHasCard c WHERE c.cardCollectionHasCardPK.cardCollectionId = :cardCollectionId"),
    @NamedQuery(name = "CardCollectionHasCard.findByCardCollectionCardCollectionTypeId", query = "SELECT c FROM CardCollectionHasCard c WHERE c.cardCollectionHasCardPK.cardCollectionCardCollectionTypeId = :cardCollectionCardCollectionTypeId"),
    @NamedQuery(name = "CardCollectionHasCard.findByCardId", query = "SELECT c FROM CardCollectionHasCard c WHERE c.cardCollectionHasCardPK.cardId = :cardId"),
    @NamedQuery(name = "CardCollectionHasCard.findByCardCardTypeId", query = "SELECT c FROM CardCollectionHasCard c WHERE c.cardCollectionHasCardPK.cardCardTypeId = :cardCardTypeId"),
    @NamedQuery(name = "CardCollectionHasCard.findByAmount", query = "SELECT c FROM CardCollectionHasCard c WHERE c.amount = :amount")})
public class CardCollectionHasCard implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CardCollectionHasCardPK cardCollectionHasCardPK;
    @Basic(optional = false)
    @Column(name = "amount", nullable = false)
    private int amount;
    @JoinColumns({
        @JoinColumn(name = "card_collection_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "card_collection_card_collection_type_id", referencedColumnName = "card_collection_type_id", nullable = false, insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private CardCollection cardCollection;
    @JoinColumns({
        @JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "card_card_type_id", referencedColumnName = "card_type_id", nullable = false, insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Card card;

    public CardCollectionHasCard() {
    }

    public CardCollectionHasCard(CardCollectionHasCardPK cardCollectionHasCardPK) {
        this.cardCollectionHasCardPK = cardCollectionHasCardPK;
    }

    public CardCollectionHasCard(CardCollectionHasCardPK cardCollectionHasCardPK, int amount) {
        this.cardCollectionHasCardPK = cardCollectionHasCardPK;
        this.amount = amount;
    }

    public CardCollectionHasCard(int cardCollectionId, int cardCollectionCardCollectionTypeId, int cardId, int cardCardTypeId, int amount) {
        this.cardCollectionHasCardPK = new CardCollectionHasCardPK(cardCollectionId, cardCollectionCardCollectionTypeId, cardId, cardCardTypeId);
        this.amount = amount;
    }

    public CardCollectionHasCardPK getCardCollectionHasCardPK() {
        return cardCollectionHasCardPK;
    }

    public void setCardCollectionHasCardPK(CardCollectionHasCardPK cardCollectionHasCardPK) {
        this.cardCollectionHasCardPK = cardCollectionHasCardPK;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public CardCollection getCardCollection() {
        return cardCollection;
    }

    public void setCardCollection(CardCollection cardCollection) {
        this.cardCollection = cardCollection;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cardCollectionHasCardPK != null ? cardCollectionHasCardPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardCollectionHasCard)) {
            return false;
        }
        CardCollectionHasCard other = (CardCollectionHasCard) object;
        if ((this.cardCollectionHasCardPK == null && other.cardCollectionHasCardPK != null) || (this.cardCollectionHasCardPK != null && !this.cardCollectionHasCardPK.equals(other.cardCollectionHasCardPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardCollectionHasCard[ cardCollectionHasCardPK=" + cardCollectionHasCardPK + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardCollectionHasCard.class.getName());
}
