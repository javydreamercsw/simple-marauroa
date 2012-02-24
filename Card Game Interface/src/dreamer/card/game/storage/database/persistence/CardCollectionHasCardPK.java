/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class CardCollectionHasCardPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "card_collection_id", nullable = false)
    private int cardCollectionId;
    @Basic(optional = false)
    @Column(name = "card_collection_card_collection_type_id", nullable = false)
    private int cardCollectionCardCollectionTypeId;
    @Basic(optional = false)
    @Column(name = "card_id", nullable = false)
    private int cardId;
    @Basic(optional = false)
    @Column(name = "card_card_type_id", nullable = false)
    private int cardCardTypeId;

    public CardCollectionHasCardPK() {
    }

    public CardCollectionHasCardPK(int cardCollectionId, int cardCollectionCardCollectionTypeId, int cardId, int cardCardTypeId) {
        this.cardCollectionId = cardCollectionId;
        this.cardCollectionCardCollectionTypeId = cardCollectionCardCollectionTypeId;
        this.cardId = cardId;
        this.cardCardTypeId = cardCardTypeId;
    }

    public int getCardCollectionId() {
        return cardCollectionId;
    }

    public void setCardCollectionId(int cardCollectionId) {
        this.cardCollectionId = cardCollectionId;
    }

    public int getCardCollectionCardCollectionTypeId() {
        return cardCollectionCardCollectionTypeId;
    }

    public void setCardCollectionCardCollectionTypeId(int cardCollectionCardCollectionTypeId) {
        this.cardCollectionCardCollectionTypeId = cardCollectionCardCollectionTypeId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getCardCardTypeId() {
        return cardCardTypeId;
    }

    public void setCardCardTypeId(int cardCardTypeId) {
        this.cardCardTypeId = cardCardTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) cardCollectionId;
        hash += (int) cardCollectionCardCollectionTypeId;
        hash += (int) cardId;
        hash += (int) cardCardTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardCollectionHasCardPK)) {
            return false;
        }
        CardCollectionHasCardPK other = (CardCollectionHasCardPK) object;
        if (this.cardCollectionId != other.cardCollectionId) {
            return false;
        }
        if (this.cardCollectionCardCollectionTypeId != other.cardCollectionCardCollectionTypeId) {
            return false;
        }
        if (this.cardId != other.cardId) {
            return false;
        }
        if (this.cardCardTypeId != other.cardCardTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardCollectionHasCardPK[ cardCollectionId=" + cardCollectionId + ", cardCollectionCardCollectionTypeId=" + cardCollectionCardCollectionTypeId + ", cardId=" + cardId + ", cardCardTypeId=" + cardCardTypeId + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardCollectionHasCardPK.class.getName());
    
}
