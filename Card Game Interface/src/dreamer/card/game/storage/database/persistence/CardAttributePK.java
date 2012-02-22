/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class CardAttributePK implements Serializable {
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardAttributeGen")
    @TableGenerator(name = "CardAttributeGen", table = "card_id",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card_attribute",
    allocationSize = 1,
    initialValue=1)
    @Column(name = "id", nullable = false)
    private int id;
    @Basic(optional = false)
    @Column(name = "card_attribute_type_id", nullable = false)
    private int cardAttributeTypeId;

    public CardAttributePK() {
    }

    public CardAttributePK(int cardAttributeTypeId) {
        this.cardAttributeTypeId = cardAttributeTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCardAttributeTypeId() {
        return cardAttributeTypeId;
    }

    public void setCardAttributeTypeId(int cardAttributeTypeId) {
        this.cardAttributeTypeId = cardAttributeTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) cardAttributeTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardAttributePK)) {
            return false;
        }
        CardAttributePK other = (CardAttributePK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.cardAttributeTypeId != other.cardAttributeTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardAttributePK[ id=" + id + ", cardAttributeTypeId=" + cardAttributeTypeId + " ]";
    }
    
}
