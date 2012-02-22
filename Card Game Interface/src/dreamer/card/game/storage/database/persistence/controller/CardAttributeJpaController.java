/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence.controller;

import dreamer.card.game.storage.database.persistence.CardAttribute;
import dreamer.card.game.storage.database.persistence.CardAttributePK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dreamer.card.game.storage.database.persistence.CardAttributeType;
import dreamer.card.game.storage.database.persistence.CardHasCardAttribute;
import dreamer.card.game.storage.database.persistence.controller.exceptions.IllegalOrphanException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CardAttributeJpaController implements Serializable {

    public CardAttributeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardAttribute cardAttribute) throws PreexistingEntityException, Exception {
        if (cardAttribute.getCardAttributePK() == null) {
            cardAttribute.setCardAttributePK(new CardAttributePK());
        }
        if (cardAttribute.getCardHasCardAttributeList() == null) {
            cardAttribute.setCardHasCardAttributeList(new ArrayList<CardHasCardAttribute>());
        }
        cardAttribute.getCardAttributePK().setCardAttributeTypeId(cardAttribute.getCardAttributeType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardAttributeType cardAttributeType = cardAttribute.getCardAttributeType();
            if (cardAttributeType != null) {
                cardAttributeType = em.getReference(cardAttributeType.getClass(), cardAttributeType.getId());
                cardAttribute.setCardAttributeType(cardAttributeType);
            }
            List<CardHasCardAttribute> attachedCardHasCardAttributeList = new ArrayList<CardHasCardAttribute>();
            for (CardHasCardAttribute cardHasCardAttributeListCardHasCardAttributeToAttach : cardAttribute.getCardHasCardAttributeList()) {
                cardHasCardAttributeListCardHasCardAttributeToAttach = em.getReference(cardHasCardAttributeListCardHasCardAttributeToAttach.getClass(), cardHasCardAttributeListCardHasCardAttributeToAttach.getCardHasCardAttributePK());
                attachedCardHasCardAttributeList.add(cardHasCardAttributeListCardHasCardAttributeToAttach);
            }
            cardAttribute.setCardHasCardAttributeList(attachedCardHasCardAttributeList);
            em.persist(cardAttribute);
            if (cardAttributeType != null) {
                cardAttributeType.getCardAttributeList().add(cardAttribute);
                cardAttributeType = em.merge(cardAttributeType);
            }
            for (CardHasCardAttribute cardHasCardAttributeListCardHasCardAttribute : cardAttribute.getCardHasCardAttributeList()) {
                CardAttribute oldCardAttributeOfCardHasCardAttributeListCardHasCardAttribute = cardHasCardAttributeListCardHasCardAttribute.getCardAttribute();
                cardHasCardAttributeListCardHasCardAttribute.setCardAttribute(cardAttribute);
                cardHasCardAttributeListCardHasCardAttribute = em.merge(cardHasCardAttributeListCardHasCardAttribute);
                if (oldCardAttributeOfCardHasCardAttributeListCardHasCardAttribute != null) {
                    oldCardAttributeOfCardHasCardAttributeListCardHasCardAttribute.getCardHasCardAttributeList().remove(cardHasCardAttributeListCardHasCardAttribute);
                    oldCardAttributeOfCardHasCardAttributeListCardHasCardAttribute = em.merge(oldCardAttributeOfCardHasCardAttributeListCardHasCardAttribute);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCardAttribute(cardAttribute.getCardAttributePK()) != null) {
                throw new PreexistingEntityException("CardAttribute " + cardAttribute + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardAttribute cardAttribute) throws IllegalOrphanException, NonexistentEntityException, Exception {
        cardAttribute.getCardAttributePK().setCardAttributeTypeId(cardAttribute.getCardAttributeType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardAttribute persistentCardAttribute = em.find(CardAttribute.class, cardAttribute.getCardAttributePK());
            CardAttributeType cardAttributeTypeOld = persistentCardAttribute.getCardAttributeType();
            CardAttributeType cardAttributeTypeNew = cardAttribute.getCardAttributeType();
            List<CardHasCardAttribute> cardHasCardAttributeListOld = persistentCardAttribute.getCardHasCardAttributeList();
            List<CardHasCardAttribute> cardHasCardAttributeListNew = cardAttribute.getCardHasCardAttributeList();
            List<String> illegalOrphanMessages = null;
            for (CardHasCardAttribute cardHasCardAttributeListOldCardHasCardAttribute : cardHasCardAttributeListOld) {
                if (!cardHasCardAttributeListNew.contains(cardHasCardAttributeListOldCardHasCardAttribute)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CardHasCardAttribute " + cardHasCardAttributeListOldCardHasCardAttribute + " since its cardAttribute field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cardAttributeTypeNew != null) {
                cardAttributeTypeNew = em.getReference(cardAttributeTypeNew.getClass(), cardAttributeTypeNew.getId());
                cardAttribute.setCardAttributeType(cardAttributeTypeNew);
            }
            List<CardHasCardAttribute> attachedCardHasCardAttributeListNew = new ArrayList<CardHasCardAttribute>();
            for (CardHasCardAttribute cardHasCardAttributeListNewCardHasCardAttributeToAttach : cardHasCardAttributeListNew) {
                cardHasCardAttributeListNewCardHasCardAttributeToAttach = em.getReference(cardHasCardAttributeListNewCardHasCardAttributeToAttach.getClass(), cardHasCardAttributeListNewCardHasCardAttributeToAttach.getCardHasCardAttributePK());
                attachedCardHasCardAttributeListNew.add(cardHasCardAttributeListNewCardHasCardAttributeToAttach);
            }
            cardHasCardAttributeListNew = attachedCardHasCardAttributeListNew;
            cardAttribute.setCardHasCardAttributeList(cardHasCardAttributeListNew);
            cardAttribute = em.merge(cardAttribute);
            if (cardAttributeTypeOld != null && !cardAttributeTypeOld.equals(cardAttributeTypeNew)) {
                cardAttributeTypeOld.getCardAttributeList().remove(cardAttribute);
                cardAttributeTypeOld = em.merge(cardAttributeTypeOld);
            }
            if (cardAttributeTypeNew != null && !cardAttributeTypeNew.equals(cardAttributeTypeOld)) {
                cardAttributeTypeNew.getCardAttributeList().add(cardAttribute);
                cardAttributeTypeNew = em.merge(cardAttributeTypeNew);
            }
            for (CardHasCardAttribute cardHasCardAttributeListNewCardHasCardAttribute : cardHasCardAttributeListNew) {
                if (!cardHasCardAttributeListOld.contains(cardHasCardAttributeListNewCardHasCardAttribute)) {
                    CardAttribute oldCardAttributeOfCardHasCardAttributeListNewCardHasCardAttribute = cardHasCardAttributeListNewCardHasCardAttribute.getCardAttribute();
                    cardHasCardAttributeListNewCardHasCardAttribute.setCardAttribute(cardAttribute);
                    cardHasCardAttributeListNewCardHasCardAttribute = em.merge(cardHasCardAttributeListNewCardHasCardAttribute);
                    if (oldCardAttributeOfCardHasCardAttributeListNewCardHasCardAttribute != null && !oldCardAttributeOfCardHasCardAttributeListNewCardHasCardAttribute.equals(cardAttribute)) {
                        oldCardAttributeOfCardHasCardAttributeListNewCardHasCardAttribute.getCardHasCardAttributeList().remove(cardHasCardAttributeListNewCardHasCardAttribute);
                        oldCardAttributeOfCardHasCardAttributeListNewCardHasCardAttribute = em.merge(oldCardAttributeOfCardHasCardAttributeListNewCardHasCardAttribute);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CardAttributePK id = cardAttribute.getCardAttributePK();
                if (findCardAttribute(id) == null) {
                    throw new NonexistentEntityException("The cardAttribute with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CardAttributePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardAttribute cardAttribute;
            try {
                cardAttribute = em.getReference(CardAttribute.class, id);
                cardAttribute.getCardAttributePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardAttribute with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CardHasCardAttribute> cardHasCardAttributeListOrphanCheck = cardAttribute.getCardHasCardAttributeList();
            for (CardHasCardAttribute cardHasCardAttributeListOrphanCheckCardHasCardAttribute : cardHasCardAttributeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CardAttribute (" + cardAttribute + ") cannot be destroyed since the CardHasCardAttribute " + cardHasCardAttributeListOrphanCheckCardHasCardAttribute + " in its cardHasCardAttributeList field has a non-nullable cardAttribute field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            CardAttributeType cardAttributeType = cardAttribute.getCardAttributeType();
            if (cardAttributeType != null) {
                cardAttributeType.getCardAttributeList().remove(cardAttribute);
                cardAttributeType = em.merge(cardAttributeType);
            }
            em.remove(cardAttribute);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardAttribute> findCardAttributeEntities() {
        return findCardAttributeEntities(true, -1, -1);
    }

    public List<CardAttribute> findCardAttributeEntities(int maxResults, int firstResult) {
        return findCardAttributeEntities(false, maxResults, firstResult);
    }

    private List<CardAttribute> findCardAttributeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardAttribute.class));
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

    public CardAttribute findCardAttribute(CardAttributePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardAttribute.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardAttributeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardAttribute> rt = cq.from(CardAttribute.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
