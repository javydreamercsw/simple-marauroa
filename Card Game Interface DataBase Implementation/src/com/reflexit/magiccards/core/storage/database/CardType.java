package com.reflexit.magiccards.core.storage.database;

import com.reflexit.magiccards.core.model.ICardType;
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
@Table(name = "card_type", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardType.findAll", query = "SELECT c FROM CardType c"),
    @NamedQuery(name = "CardType.findById", query = "SELECT c FROM CardType c WHERE c.id = :id"),
    @NamedQuery(name = "CardType.findByName", query = "SELECT c FROM CardType c WHERE c.name = :name")})
public class CardType implements Serializable, ICardType {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardTypeGen")
    @TableGenerator(name = "CardTypeGen", table = "card_id",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card_type",
    allocationSize = 1,
    initialValue=1)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 80)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cardType")
    private List<Card> cardList;

    public CardType() {
    }

    public CardType(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardType)) {
            return false;
        }
        CardType other = (CardType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardType[ id=" + id + " ]";
    }
    private static final Logger LOG = Logger.getLogger(CardType.class.getName());
    
}
