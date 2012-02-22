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
@Table(name = "card_attribute_type", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CardAttributeType.findAll", query = "SELECT c FROM CardAttributeType c"),
    @NamedQuery(name = "CardAttributeType.findById", query = "SELECT c FROM CardAttributeType c WHERE c.id = :id"),
    @NamedQuery(name = "CardAttributeType.findByName", query = "SELECT c FROM CardAttributeType c WHERE c.name = :name")})
public class CardAttributeType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CardAttributeTypeGen")
    @TableGenerator(name = "CardAttributeTypeGen", table = "card_id",
    pkColumnName = "tablename",
    valueColumnName = "last_id",
    pkColumnValue = "card_attribute_type",
    allocationSize = 1,
    initialValue=1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cardAttributeType")
    private List<CardAttribute> cardAttributeList;

    public CardAttributeType() {
    }
    
    public CardAttributeType(String name) {
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
    public List<CardAttribute> getCardAttributeList() {
        return cardAttributeList;
    }

    public void setCardAttributeList(List<CardAttribute> cardAttributeList) {
        this.cardAttributeList = cardAttributeList;
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
        if (!(object instanceof CardAttributeType)) {
            return false;
        }
        CardAttributeType other = (CardAttributeType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dreamer.card.game.storage.database.persistence.CardAttributeType[ id=" + id + " ]";
    }
    
}
