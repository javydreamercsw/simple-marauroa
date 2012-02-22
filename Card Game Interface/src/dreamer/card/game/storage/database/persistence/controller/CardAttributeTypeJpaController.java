/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dreamer.card.game.storage.database.persistence.CardAttribute;
import dreamer.card.game.storage.database.persistence.CardAttributeType;
import dreamer.card.game.storage.database.persistence.controller.exceptions.IllegalOrphanException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CardAttributeTypeJpaController implements Serializable {

    public CardAttributeTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardAttributeType cardAttributeType) {
        if (cardAttributeType.getCardAttributeList() == null) {
            cardAttributeType.setCardAttributeList(new ArrayList<CardAttribute>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CardAttribute> attachedCardAttributeList = new ArrayList<CardAttribute>();
            for (CardAttribute cardAttributeListCardAttributeToAttach : cardAttributeType.getCardAttributeList()) {
                cardAttributeListCardAttributeToAttach = em.getReference(cardAttributeListCardAttributeToAttach.getClass(), cardAttributeListCardAttributeToAttach.getCardAttributePK());
                attachedCardAttributeList.add(cardAttributeListCardAttributeToAttach);
            }
            cardAttributeType.setCardAttributeList(attachedCardAttributeList);
            em.persist(cardAttributeType);
            for (CardAttribute cardAttributeListCardAttribute : cardAttributeType.getCardAttributeList()) {
                CardAttributeType oldCardAttributeTypeOfCardAttributeListCardAttribute = cardAttributeListCardAttribute.getCardAttributeType();
                cardAttributeListCardAttribute.setCardAttributeType(cardAttributeType);
                cardAttributeListCardAttribute = em.merge(cardAttributeListCardAttribute);
                if (oldCardAttributeTypeOfCardAttributeListCardAttribute != null) {
                    oldCardAttributeTypeOfCardAttributeListCardAttribute.getCardAttributeList().remove(cardAttributeListCardAttribute);
                    oldCardAttributeTypeOfCardAttributeListCardAttribute = em.merge(oldCardAttributeTypeOfCardAttributeListCardAttribute);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardAttributeType cardAttributeType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardAttributeType persistentCardAttributeType = em.find(CardAttributeType.class, cardAttributeType.getId());
            List<CardAttribute> cardAttributeListOld = persistentCardAttributeType.getCardAttributeList();
            List<CardAttribute> cardAttributeListNew = cardAttributeType.getCardAttributeList();
            List<String> illegalOrphanMessages = null;
            for (CardAttribute cardAttributeListOldCardAttribute : cardAttributeListOld) {
                if (!cardAttributeListNew.contains(cardAttributeListOldCardAttribute)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CardAttribute " + cardAttributeListOldCardAttribute + " since its cardAttributeType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CardAttribute> attachedCardAttributeListNew = new ArrayList<CardAttribute>();
            for (CardAttribute cardAttributeListNewCardAttributeToAttach : cardAttributeListNew) {
                cardAttributeListNewCardAttributeToAttach = em.getReference(cardAttributeListNewCardAttributeToAttach.getClass(), cardAttributeListNewCardAttributeToAttach.getCardAttributePK());
                attachedCardAttributeListNew.add(cardAttributeListNewCardAttributeToAttach);
            }
            cardAttributeListNew = attachedCardAttributeListNew;
            cardAttributeType.setCardAttributeList(cardAttributeListNew);
            cardAttributeType = em.merge(cardAttributeType);
            for (CardAttribute cardAttributeListNewCardAttribute : cardAttributeListNew) {
                if (!cardAttributeListOld.contains(cardAttributeListNewCardAttribute)) {
                    CardAttributeType oldCardAttributeTypeOfCardAttributeListNewCardAttribute = cardAttributeListNewCardAttribute.getCardAttributeType();
                    cardAttributeListNewCardAttribute.setCardAttributeType(cardAttributeType);
                    cardAttributeListNewCardAttribute = em.merge(cardAttributeListNewCardAttribute);
                    if (oldCardAttributeTypeOfCardAttributeListNewCardAttribute != null && !oldCardAttributeTypeOfCardAttributeListNewCardAttribute.equals(cardAttributeType)) {
                        oldCardAttributeTypeOfCardAttributeListNewCardAttribute.getCardAttributeList().remove(cardAttributeListNewCardAttribute);
                        oldCardAttributeTypeOfCardAttributeListNewCardAttribute = em.merge(oldCardAttributeTypeOfCardAttributeListNewCardAttribute);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cardAttributeType.getId();
                if (findCardAttributeType(id) == null) {
                    throw new NonexistentEntityException("The cardAttributeType with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardAttributeType cardAttributeType;
            try {
                cardAttributeType = em.getReference(CardAttributeType.class, id);
                cardAttributeType.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardAttributeType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CardAttribute> cardAttributeListOrphanCheck = cardAttributeType.getCardAttributeList();
            for (CardAttribute cardAttributeListOrphanCheckCardAttribute : cardAttributeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CardAttributeType (" + cardAttributeType + ") cannot be destroyed since the CardAttribute " + cardAttributeListOrphanCheckCardAttribute + " in its cardAttributeList field has a non-nullable cardAttributeType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cardAttributeType);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardAttributeType> findCardAttributeTypeEntities() {
        return findCardAttributeTypeEntities(true, -1, -1);
    }

    public List<CardAttributeType> findCardAttributeTypeEntities(int maxResults, int firstResult) {
        return findCardAttributeTypeEntities(false, maxResults, firstResult);
    }

    private List<CardAttributeType> findCardAttributeTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardAttributeType.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public CardAttributeType findCardAttributeType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardAttributeType.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardAttributeTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardAttributeType> rt = cq.from(CardAttributeType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
