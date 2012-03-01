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
@Table(name = "card_attribute", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardAttribute.findAll", query = "SELECT c FROM CardAttribute c"),
    @NamedQuery(name = "CardAttribute.findById", query = "SELECT c FROM CardAttribute c WHERE c.id = :id"),
    @NamedQuery(name = "CardAttribute.findByName", query = "SELECT c FROM CardAttribute c WHERE c.name = :name")})
public class CardAttribute implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cardAttribute")
    private List<CardHasCardAttribute> cardHasCardAttributeList;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardAttributeGen")
    @TableGenerator(name = "CardAttributeGen", table = "card_attribute",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card",
    allocationSize = 1,
    initialValue=1)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    public CardAttribute() {
    }

    public CardAttribute(Integer id) {
        this.id = id;
    }

    public CardAttribute(Integer id, String name) {
        this.id = id;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CardAttribute)) {
            return false;
        }
        CardAttribute other = (CardAttribute) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardAttribute[ id=" + id + " ]";
    }

    @XmlTransient
    public List<CardHasCardAttribute> getCardHasCardAttributeList() {
        return cardHasCardAttributeList;
    }

    public void setCardHasCardAttributeList(List<CardHasCardAttribute> cardHasCardAttributeList) {
        this.cardHasCardAttributeList = cardHasCardAttributeList;
    }
    
}
