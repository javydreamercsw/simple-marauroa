/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "card_attribute", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardAttribute.findAll", query = "SELECT c FROM CardAttribute c"),
    @NamedQuery(name = "CardAttribute.findById", query = "SELECT c FROM CardAttribute c WHERE c.cardAttributePK.id = :id"),
    @NamedQuery(name = "CardAttribute.findByCardAttributeTypeId", query = "SELECT c FROM CardAttribute c WHERE c.cardAttributePK.cardAttributeTypeId = :cardAttributeTypeId"),
    @NamedQuery(name = "CardAttribute.findByName", query = "SELECT c FROM CardAttribute c WHERE c.name = :name")})
public class CardAttribute implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CardAttributePK cardAttributePK;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    public CardAttribute() {
    }

    public CardAttribute(CardAttributePK cardAttributePK) {
        this.cardAttributePK = cardAttributePK;
    }

    public CardAttribute(CardAttributePK cardAttributePK, String name) {
        this.cardAttributePK = cardAttributePK;
        this.name = name;
    }

    public CardAttribute(int cardAttributeTypeId) {
        this.cardAttributePK = new CardAttributePK(cardAttributeTypeId);
    }

    public CardAttributePK getCardAttributePK() {
        return cardAttributePK;
    }

    public void setCardAttributePK(CardAttributePK cardAttributePK) {
        this.cardAttributePK = cardAttributePK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cardAttributePK != null ? cardAttributePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardAttribute)) {
            return false;
        }
        CardAttribute other = (CardAttribute) object;
        if ((this.cardAttributePK == null && other.cardAttributePK != null) || (this.cardAttributePK != null && !this.cardAttributePK.equals(other.cardAttributePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardAttribute[ cardAttributePK=" + cardAttributePK + " ]";
    }
    
}
