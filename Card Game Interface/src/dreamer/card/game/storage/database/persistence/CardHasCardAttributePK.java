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
    @Basic(optional = false)
    @Column(name = "card_attribute_card_attribute_type_id", nullable = false)
    private int cardAttributeCardAttributeTypeId;

    public CardHasCardAttributePK() {
    }

    public CardHasCardAttributePK(int cardId, int cardCardTypeId, int cardAttributeId, int cardAttributeCardAttributeTypeId) {
        this.cardId = cardId;
        this.cardCardTypeId = cardCardTypeId;
        this.cardAttributeId = cardAttributeId;
        this.cardAttributeCardAttributeTypeId = cardAttributeCardAttributeTypeId;
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

    public int getCardAttributeCardAttributeTypeId() {
        return cardAttributeCardAttributeTypeId;
    }

    public void setCardAttributeCardAttributeTypeId(int cardAttributeCardAttributeTypeId) {
        this.cardAttributeCardAttributeTypeId = cardAttributeCardAttributeTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) cardId;
        hash += (int) cardCardTypeId;
        hash += (int) cardAttributeId;
        hash += (int) cardAttributeCardAttributeTypeId;
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
        if (this.cardAttributeCardAttributeTypeId != other.cardAttributeCardAttributeTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardHasCardAttributePK[ cardId=" + cardId + ", cardCardTypeId=" + cardCardTypeId + ", cardAttributeId=" + cardAttributeId + ", cardAttributeCardAttributeTypeId=" + cardAttributeCardAttributeTypeId + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardHasCardAttributePK.class.getName());
    
}
