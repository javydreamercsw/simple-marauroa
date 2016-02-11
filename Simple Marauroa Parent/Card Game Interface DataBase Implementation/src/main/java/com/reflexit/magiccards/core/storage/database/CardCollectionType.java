/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database;

import com.reflexit.magiccards.core.model.ICardCollectionType;
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
@Table(name = "card_collection_type", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardCollectionType.findAll", query = "SELECT c FROM CardCollectionType c"),
    @NamedQuery(name = "CardCollectionType.findById", query = "SELECT c FROM CardCollectionType c WHERE c.id = :id"),
    @NamedQuery(name = "CardCollectionType.findByName", query = "SELECT c FROM CardCollectionType c WHERE c.name = :name")})
public class CardCollectionType implements Serializable, ICardCollectionType {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardCollectionTypeGen")
    @TableGenerator(name = "CardCollectionTypeGen", table = "card_id",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card_collection_type",
    allocationSize = 1,
    initialValue=1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "name", length = 45)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cardCollectionType")
    private List<CardCollection> cardCollectionList;

    public CardCollectionType() {
    }

    public CardCollectionType(String name) {
        setName(name);
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<CardCollection> getCardCollectionList() {
        return cardCollectionList;
    }

    public void setCardCollectionList(List<CardCollection> cardCollectionList) {
        this.cardCollectionList = cardCollectionList;
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
        if (!(object instanceof CardCollectionType)) {
            return false;
        }
        CardCollectionType other = (CardCollectionType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardCollectionType[ id=" + id + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardCollectionType.class.getName());
    
}
