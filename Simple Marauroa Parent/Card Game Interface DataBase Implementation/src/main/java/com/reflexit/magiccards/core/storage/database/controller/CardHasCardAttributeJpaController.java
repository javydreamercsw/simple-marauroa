/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.reflexit.magiccards.core.storage.database.Card;
import com.reflexit.magiccards.core.storage.database.CardHasCardAttribute;
import com.reflexit.magiccards.core.storage.database.CardHasCardAttributePK;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.NonexistentEntityException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.PreexistingEntityException;
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
        cardHasCardAttribute.getCardHasCardAttributePK().setCardId(cardHasCardAttribute.getCard().getCardPK().getId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardCardTypeId(cardHasCardAttribute.getCard().getCardPK().getCardTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Card card = cardHasCardAttribute.getCard();
            if (card != null) {
                card = em.getReference(card.getClass(), card.getCardPK());
                cardHasCardAttribute.setCard(card);
            }
            em.persist(cardHasCardAttribute);
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
        cardHasCardAttribute.getCardHasCardAttributePK().setCardId(cardHasCardAttribute.getCard().getCardPK().getId());
        cardHasCardAttribute.getCardHasCardAttributePK().setCardCardTypeId(cardHasCardAttribute.getCard().getCardPK().getCardTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardHasCardAttribute persistentCardHasCardAttribute = em.find(CardHasCardAttribute.class, cardHasCardAttribute.getCardHasCardAttributePK());
            Card cardOld = persistentCardHasCardAttribute.getCard();
            Card cardNew = cardHasCardAttribute.getCard();
            if (cardNew != null) {
                cardNew = em.getReference(cardNew.getClass(), cardNew.getCardPK());
                cardHasCardAttribute.setCard(cardNew);
            }
            cardHasCardAttribute = em.merge(cardHasCardAttribute);
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