/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database;

import com.reflexit.magiccards.core.model.ICardSet;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
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
@Table(name = "card_set")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardSet.findAll", query = "SELECT c FROM CardSet c"),
    @NamedQuery(name = "CardSet.findById", query = "SELECT c FROM CardSet c WHERE c.cardSetPK.id = :id"),
    @NamedQuery(name = "CardSet.findByGameId", query = "SELECT c FROM CardSet c WHERE c.cardSetPK.gameId = :gameId"),
    @NamedQuery(name = "CardSet.findByAbbreviation", query = "SELECT c FROM CardSet c WHERE c.abbreviation = :abbreviation"),
    @NamedQuery(name = "CardSet.findByName", query = "SELECT c FROM CardSet c WHERE c.name = :name"),
    @NamedQuery(name = "CardSet.findByReleased", query = "SELECT c FROM CardSet c WHERE c.released = :released")})
public class CardSet implements Serializable, ICardSet{
    @Basic(optional = false)
    @Column(name = "released", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date released;
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CardSetPK cardSetPK;
    @Basic(optional = false)
    @Column(name = "abbreviation", nullable = false, length = 80)
    private String abbreviation;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 80)
    private String name;
    @JoinTable(name = "card_set_has_card", joinColumns = {
        @JoinColumn(name = "card_set_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "card_set_game_id", referencedColumnName = "game_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "card_card_type_id", referencedColumnName = "card_type_id", nullable = false)})
    @ManyToMany
    private List<Card> cardList;
    @JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Game game;

    public CardSet() {
    }

    public CardSet(CardSetPK cardSetPK) {
        this.cardSetPK = cardSetPK;
    }

    public CardSet(CardSetPK cardSetPK, String abbreviation, String name, Date released) {
        this.cardSetPK = cardSetPK;
        this.abbreviation = abbreviation;
        this.name = name;
        this.released = released;
    }

    public CardSet(int gameId, String abbreviation, String name, Date released) {
        this.cardSetPK = new CardSetPK(gameId);
        this.abbreviation = abbreviation;
        this.name = name;
        this.released = released;
    }

    public CardSetPK getCardSetPK() {
        return cardSetPK;
    }

    public void setCardSetPK(CardSetPK cardSetPK) {
        this.cardSetPK = cardSetPK;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cardSetPK != null ? cardSetPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardSet)) {
            return false;
        }
        CardSet other = (CardSet) object;
        if ((this.cardSetPK == null && other.cardSetPK != null) || (this.cardSetPK != null && !this.cardSetPK.equals(other.cardSetPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardSet[ cardSetPK=" + cardSetPK + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardSet.class.getName());

    public Date getReleased() {
        return released;
    }

    public void setReleased(Date released) {
        this.released = released;
    }

    @Override
    public Iterator iterator() {
        return getCardList().iterator();
    }

    @Override
    public String getGameName() {
        return getGame().getName();
    }
}
