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
import dreamer.card.game.storage.database.persistence.Card;
import dreamer.card.game.storage.database.persistence.CardHasCardAttribute;
import dreamer.card.game.storage.database.persistence.CardHasCardAttributePK;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CardHasCardAttributeJpaController implements Serializable {

    public CardHasCardAttributeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardHasCardAttribute cardHasCardAttribute) throws PreexistingEntityException, Exception {
        if (cardHasCardAttribute.getCardHasCardAttributePK() == null) {
            cardHasCardAttribute.setCardHasCardAttributePK(new CardHasCardAttributePK());
        }
        cardHasCardAttribute.getCardHasCardAttributePK().setCardCardTypeId(cardHasCardAttribute.getCard().getCardPK().getCardTypeId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardId(cardHasCardAttribute.getCard().getCardPK().getId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardAttributeCardAttributeTypeId(cardHasCardAttribute.getCardAttribute().getCardAttributePK().getCardAttributeTypeId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardAttributeId(cardHasCardAttribute.getCardAttribute().getCardAttributePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardAttribute cardAttribute = cardHasCardAttribute.getCardAttribute();
            if (cardAttribute != null) {
                cardAttribute = em.getReference(cardAttribute.getClass(), cardAttribute.getCardAttributePK());
                cardHasCardAttribute.setCardAttribute(cardAttribute);
            }
            Card card = cardHasCardAttribute.getCard();
            if (card != null) {
                card = em.getReference(card.getClass(), card.getCardPK());
                cardHasCardAttribute.setCard(card);
            }
            em.persist(cardHasCardAttribute);
            if (cardAttribute != null) {
                cardAttribute.getCardHasCardAttributeList().add(cardHasCardAttribute);
                cardAttribute = em.merge(cardAttribute);
            }
            if (card != null) {
                card.getCardHasCardAttributeList().add(cardHasCardAttribute);
                card = em.merge(card);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCardHasCardAttribute(cardHasCardAttribute.getCardHasCardAttributePK()) != null) {
                throw new PreexistingEntityException("CardHasCardAttribute " + cardHasCardAttribute + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardHasCardAttribute cardHasCardAttribute) throws NonexistentEntityException, Exception {
        cardHasCardAttribute.getCardHasCardAttributePK().setCardCardTypeId(cardHasCardAttribute.getCard().getCardPK().getCardTypeId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardId(cardHasCardAttribute.getCard().getCardPK().getId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardAttributeCardAttributeTypeId(cardHasCardAttribute.getCardAttribute().getCardAttributePK().getCardAttributeTypeId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardAttributeId(cardHasCardAttribute.getCardAttribute().getCardAttributePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardHasCardAttribute persistentCardHasCardAttribute = em.find(CardHasCardAttribute.class, cardHasCardAttribute.getCardHasCardAttributePK());
            CardAttribute cardAttributeOld = persistentCardHasCardAttribute.getCardAttribute();
            CardAttribute cardAttributeNew = cardHasCardAttribute.getCardAttribute();
            Card cardOld = persistentCardHasCardAttribute.getCard();
            Card cardNew = cardHasCardAttribute.getCard();
            if (cardAttributeNew != null) {
                cardAttributeNew = em.getReference(cardAttributeNew.getClass(), cardAttributeNew.getCardAttributePK());
                cardHasCardAttribute.setCardAttribute(cardAttributeNew);
            }
            if (cardNew != null) {
                cardNew = em.getReference(cardNew.getClass(), cardNew.getCardPK());
                cardHasCardAttribute.setCard(cardNew);
            }
            cardHasCardAttribute = em.merge(cardHasCardAttribute);
            if (cardAttributeOld != null && !cardAttributeOld.equals(cardAttributeNew)) {
                cardAttributeOld.getCardHasCardAttributeList().remove(cardHasCardAttribute);
                cardAttributeOld = em.merge(cardAttributeOld);
            }
            if (cardAttributeNew != null && !cardAttributeNew.equals(cardAttributeOld)) {
                cardAttributeNew.getCardHasCardAttributeList().add(cardHasCardAttribute);
                cardAttributeNew = em.merge(cardAttributeNew);
            }
            if (cardOld != null && !cardOld.equals(cardNew)) {
                cardOld.getCardHasCardAttributeList().remove(cardHasCardAttribute);
                cardOld = em.merge(cardOld);
            }
            if (cardNew != null && !cardNew.equals(cardOld)) {
                cardNew.getCardHasCardAttributeList().add(cardHasCardAttribute);
                cardNew = em.merge(cardNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CardHasCardAttributePK id = cardHasCardAttribute.getCardHasCardAttributePK();
                if (findCardHasCardAttribute(id) == null) {
                    throw new NonexistentEntityException("The cardHasCardAttribute with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CardHasCardAttributePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardHasCardAttribute cardHasCardAttribute;
            try {
                cardHasCardAttribute = em.getReference(CardHasCardAttribute.class, id);
                cardHasCardAttribute.getCardHasCardAttributePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardHasCardAttribute with id " + id + " no longer exists.", enfe);
            }
            CardAttribute cardAttribute = cardHasCardAttribute.getCardAttribute();
            if (cardAttribute != null) {
                cardAttribute.getCardHasCardAttributeList().remove(cardHasCardAttribute);
                cardAttribute = em.merge(cardAttribute);
            }
            Card card = cardHasCardAttribute.getCard();
            if (card != null) {
                card.getCardHasCardAttributeList().remove(cardHasCardAttribute);
                card = em.merge(card);
            }
            em.remove(cardHasCardAttribute);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardHasCardAttribute> findCardHasCardAttributeEntities() {
        return findCardHasCardAttributeEntities(true, -1, -1);
    }

    public List<CardHasCardAttribute> findCardHasCardAttributeEntities(int maxResults, int firstResult) {
        return findCardHasCardAttributeEntities(false, maxResults, firstResult);
    }

    private List<CardHasCardAttribute> findCardHasCardAttributeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardHasCardAttribute.class));
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

    public CardHasCardAttribute findCardHasCardAttribute(CardHasCardAttributePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardHasCardAttribute.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardHasCardAttributeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardHasCardAttribute> rt = cq.from(CardHasCardAttribute.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
