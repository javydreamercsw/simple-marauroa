/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database;

import com.reflexit.magiccards.core.model.IGame;
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
@Table(name = "game", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Game.findAll", query = "SELECT g FROM Game g"),
    @NamedQuery(name = "Game.findById", query = "SELECT g FROM Game g WHERE g.id = :id"),
    @NamedQuery(name = "Game.findByName", query = "SELECT g FROM Game g WHERE g.name = :name")})
public class Game implements Serializable, IGame {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardGameGen")
    @TableGenerator(name = "CardGameGen", table = "card_id",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card_game",
    allocationSize = 1,
    initialValue=1)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 80)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    private List<CardSet> cardSetList;

    public Game() {
    }

    public Game(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<CardSet> getCardSetList() {
        return cardSetList;
    }

    public void setCardSetList(List<CardSet> cardSetList) {
        this.cardSetList = cardSetList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Game)) {
            return false;
        }
        Game other = (Game) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.Game[ id=" + id + " ]";
    }
    private static final Logger LOG = Logger.getLogger(Game.class.getName());
    
}
