/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "card")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Card.findAll", query = "SELECT c FROM Card c"),
    @NamedQuery(name = "Card.findById", query = "SELECT c FROM Card c WHERE c.cardPK.id = :id"),
    @NamedQuery(name = "Card.findByCardTypeId", query = "SELECT c FROM Card c WHERE c.cardPK.cardTypeId = :cardTypeId"),
    @NamedQuery(name = "Card.findByName", query = "SELECT c FROM Card c WHERE c.name = :name")})
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CardPK cardPK;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 80)
    private String name;
    @Basic(optional = false)
    @Lob
    @Column(name = "text", nullable = false)
    private byte[] text;
    @ManyToMany(mappedBy = "cardList")
    private List<CardSet> cardSetList;
    @JoinColumn(name = "card_type_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private CardType cardType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
    private List<CardHasCardAttribute> cardHasCardAttributeList;

    public Card() {
    }

    public Card(CardPK cardPK) {
        this.cardPK = cardPK;
    }

    public Card(CardPK cardPK, String name, byte[] text) {
        this.cardPK = cardPK;
        this.name = name;
        this.text = text;
    }

    public Card(int id, int cardTypeId) {
        this.cardPK = new CardPK(id, cardTypeId);
    }

    public CardPK getCardPK() {
        return cardPK;
    }

    public void setCardPK(CardPK cardPK) {
        this.cardPK = cardPK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getText() {
        return text;
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    @XmlTransient
    public List<CardSet> getCardSetList() {
        return cardSetList;
    }

    public void setCardSetList(List<CardSet> cardSetList) {
        this.cardSetList = cardSetList;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    @XmlTransient
    public List<CardHasCardAttribute> getCardHasCardAttributeList() {
        return cardHasCardAttributeList;
    }

    public void setCardHasCardAttributeList(List<CardHasCardAttribute> cardHasCardAttributeList) {
        this.cardHasCardAttributeList = cardHasCardAttributeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cardPK != null ? cardPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Card)) {
            return false;
        }
        Card other = (Card) object;
        if ((this.cardPK == null && other.cardPK != null) || (this.cardPK != null && !this.cardPK.equals(other.cardPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.Card[ cardPK=" + cardPK + " ]";
    }
    
}
