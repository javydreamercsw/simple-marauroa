/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.*;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class CardSetPK implements Serializable {
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardSetGen")
    @TableGenerator(name = "CardSetGen", table = "card_id",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card_set",
    allocationSize = 1,
    initialValue=1)
    @Column(name = "id", nullable = false)
    private int id;
    @Basic(optional = false)
    @Column(name = "game_id", nullable = false)
    private int gameId;

    public CardSetPK() {
    }

    public CardSetPK(int gameId) {
        this.gameId = gameId;
    }

    public int getId() {
        return id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) gameId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardSetPK)) {
            return false;
        }
        CardSetPK other = (CardSetPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.gameId != other.gameId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardSetPK[ id=" + id + ", gameId=" + gameId + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardSetPK.class.getName());
    
}
