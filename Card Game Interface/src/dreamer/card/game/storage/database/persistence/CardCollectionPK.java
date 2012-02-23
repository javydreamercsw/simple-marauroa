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
public class CardCollectionPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardCollectionGen")
    @TableGenerator(name = "CardCollectionGen", table = "card_id",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card_collection",
    allocationSize = 1,
    initialValue=1)
    private int id;
    @Basic(optional = false)
    @Column(name = "card_collection_type_id", nullable = false)
    private int cardCollectionTypeId;

    public CardCollectionPK() {
    }

    public CardCollectionPK(int cardCollectionTypeId) {
        this.cardCollectionTypeId = cardCollectionTypeId;
    }

    public int getId() {
        return id;
    }

    public int getCardCollectionTypeId() {
        return cardCollectionTypeId;
    }

    public void setCardCollectionTypeId(int cardCollectionTypeId) {
        this.cardCollectionTypeId = cardCollectionTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) cardCollectionTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardCollectionPK)) {
            return false;
        }
        CardCollectionPK other = (CardCollectionPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.cardCollectionTypeId != other.cardCollectionTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardCollectionPK[ id=" + id + ", cardCollectionTypeId=" + cardCollectionTypeId + " ]";
    }
}
