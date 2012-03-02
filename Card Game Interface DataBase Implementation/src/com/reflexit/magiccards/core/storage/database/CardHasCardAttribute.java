/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database;

import com.reflexit.magiccards.core.model.ICardHasCardAttribute;
import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "card_has_card_attribute")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardHasCardAttribute.findAll", query = "SELECT c FROM CardHasCardAttribute c"),
    @NamedQuery(name = "CardHasCardAttribute.findByCardId", query = "SELECT c FROM CardHasCardAttribute c WHERE c.cardHasCardAttributePK.cardId = :cardId"),
    @NamedQuery(name = "CardHasCardAttribute.findByCardCardTypeId", query = "SELECT c FROM CardHasCardAttribute c WHERE c.cardHasCardAttributePK.cardCardTypeId = :cardCardTypeId"),
    @NamedQuery(name = "CardHasCardAttribute.findByCardAttributeId", query = "SELECT c FROM CardHasCardAttribute c WHERE c.cardHasCardAttributePK.cardAttributeId = :cardAttributeId"),
    @NamedQuery(name = "CardHasCardAttribute.findByValue", query = "SELECT c FROM CardHasCardAttribute c WHERE c.value = :value")})
public class CardHasCardAttribute implements Serializable, ICardHasCardAttribute {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CardHasCardAttributePK cardHasCardAttributePK;
    @Basic(optional = false)
    @Column(name = "value", nullable = false, length = 80)
    private String value;
    @JoinColumn(name = "card_attribute_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private CardAttribute cardAttribute;
    @JoinColumns({
        @JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "card_card_type_id", referencedColumnName = "card_type_id", nullable = false, insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Card card;

    public CardHasCardAttribute() {
    }

    public CardHasCardAttribute(CardHasCardAttributePK cardHasCardAttributePK) {
        this.cardHasCardAttributePK = cardHasCardAttributePK;
    }

    public CardHasCardAttribute(CardHasCardAttributePK cardHasCardAttributePK, String value) {
        this.cardHasCardAttributePK = cardHasCardAttributePK;
        this.value = value;
    }

    public CardHasCardAttribute(int cardId, int cardCardTypeId, int cardAttributeId) {
        this.cardHasCardAttributePK = new CardHasCardAttributePK(cardId, cardCardTypeId, cardAttributeId);
    }

    public CardHasCardAttributePK getCardHasCardAttributePK() {
        return cardHasCardAttributePK;
    }

    public void setCardHasCardAttributePK(CardHasCardAttributePK cardHasCardAttributePK) {
        this.cardHasCardAttributePK = cardHasCardAttributePK;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CardAttribute getCardAttribute() {
        return cardAttribute;
    }

    public void setCardAttribute(CardAttribute cardAttribute) {
        this.cardAttribute = cardAttribute;
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
        hash += (cardHasCardAttributePK != null ? cardHasCardAttributePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardHasCardAttribute)) {
            return false;
        }
        CardHasCardAttribute other = (CardHasCardAttribute) object;
        if ((this.cardHasCardAttributePK == null && other.cardHasCardAttributePK != null) || (this.cardHasCardAttributePK != null && !this.cardHasCardAttributePK.equals(other.cardHasCardAttributePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardHasCardAttribute[ cardHasCardAttributePK=" + cardHasCardAttributePK + " ]";
    }
    
}
