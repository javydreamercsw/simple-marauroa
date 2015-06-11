/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database;

import com.reflexit.magiccards.core.model.ICardCollection;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "card_collection", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardCollection.findAll", query = "SELECT c FROM CardCollection c"),
    @NamedQuery(name = "CardCollection.findById", query = "SELECT c FROM CardCollection c WHERE c.cardCollectionPK.id = :id"),
    @NamedQuery(name = "CardCollection.findByCardCollectionTypeId", query = "SELECT c FROM CardCollection c WHERE c.cardCollectionPK.cardCollectionTypeId = :cardCollectionTypeId"),
    @NamedQuery(name = "CardCollection.findByName", query = "SELECT c FROM CardCollection c WHERE c.name = :name")})
public class CardCollection implements Serializable, ICardCollection {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CardCollectionPK cardCollectionPK;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 80)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cardCollection")
    private List<CardCollectionHasCard> cardCollectionHasCardList;
    @JoinColumn(name = "card_collection_type_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private CardCollectionType cardCollectionType;

    public CardCollection() {
    }

    public CardCollection(CardCollectionPK cardCollectionPK) {
        this.cardCollectionPK = cardCollectionPK;
    }

    public CardCollection(CardCollectionPK cardCollectionPK, String name) {
        this.cardCollectionPK = cardCollectionPK;
        this.name = name;
    }

    public CardCollection(int cardCollectionTypeId, String name) {
        this.cardCollectionPK = new CardCollectionPK(cardCollectionTypeId);
        this.name = name;
    }

    public CardCollectionPK getCardCollectionPK() {
        return cardCollectionPK;
    }

    public void setCardCollectionPK(CardCollectionPK cardCollectionPK) {
        this.cardCollectionPK = cardCollectionPK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<CardCollectionHasCard> getCardCollectionHasCardList() {
        return cardCollectionHasCardList;
    }

    public void setCardCollectionHasCardList(List<CardCollectionHasCard> cardCollectionHasCardList) {
        this.cardCollectionHasCardList = cardCollectionHasCardList;
    }

    public CardCollectionType getCardCollectionType() {
        return cardCollectionType;
    }

    public void setCardCollectionType(CardCollectionType cardCollectionType) {
        this.cardCollectionType = cardCollectionType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cardCollectionPK != null ? cardCollectionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardCollection)) {
            return false;
        }
        CardCollection other = (CardCollection) object;
        if ((this.cardCollectionPK == null && other.cardCollectionPK != null) || (this.cardCollectionPK != null && !this.cardCollectionPK.equals(other.cardCollectionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardCollection[ cardCollectionPK=" + cardCollectionPK + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardCollection.class.getName());
}
