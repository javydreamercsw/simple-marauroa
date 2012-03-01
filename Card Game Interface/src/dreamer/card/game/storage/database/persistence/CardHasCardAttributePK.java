/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class CardHasCardAttributePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "card_id", nullable = false)
    private int cardId;
    @Basic(optional = false)
    @Column(name = "card_card_type_id", nullable = false)
    private int cardCardTypeId;
    @Basic(optional = false)
    @Column(name = "card_attribute_id", nullable = false)
    private int cardAttributeId;

    public CardHasCardAttributePK() {
    }

    public CardHasCardAttributePK(int cardId, int cardCardTypeId, int cardAttributeId) {
        this.cardId = cardId;
        this.cardCardTypeId = cardCardTypeId;
        this.cardAttributeId = cardAttributeId;
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

    public int getCardAttributeId() {
        return cardAttributeId;
    }

    public void setCardAttributeId(int cardAttributeId) {
        this.cardAttributeId = cardAttributeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) cardId;
        hash += (int) cardCardTypeId;
        hash += (int) cardAttributeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardHasCardAttributePK)) {
            return false;
        }
        CardHasCardAttributePK other = (CardHasCardAttributePK) object;
        if (this.cardId != other.cardId) {
            return false;
        }
        if (this.cardCardTypeId != other.cardCardTypeId) {
            return false;
        }
        if (this.cardAttributeId != other.cardAttributeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardHasCardAttributePK[ cardId=" + cardId + ", cardCardTypeId=" + cardCardTypeId + ", cardAttributeId=" + cardAttributeId + " ]";
    }
    
}
