/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence.controller;

import dreamer.card.game.storage.database.persistence.CardCollection;
import dreamer.card.game.storage.database.persistence.CardCollectionPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dreamer.card.game.storage.database.persistence.CardCollectionType;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CardCollectionJpaController implements Serializable {

    public CardCollectionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardCollection cardCollection) throws PreexistingEntityException, Exception {
        if (cardCollection.getCardCollectionPK() == null) {
            cardCollection.setCardCollectionPK(new CardCollectionPK());
        }
        cardCollection.getCardCollectionPK().setCardCollectionTypeId(cardCollection.getCardCollectionType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardCollectionType cardCollectionType = cardCollection.getCardCollectionType();
            if (cardCollectionType != null) {
                cardCollectionType = em.getReference(cardCollectionType.getClass(), cardCollectionType.getId());
                cardCollection.setCardCollectionType(cardCollectionType);
            }
            em.persist(cardCollection);
            if (cardCollectionType != null) {
                cardCollectionType.getCardCollectionList().add(cardCollection);
                cardCollectionType = em.merge(cardCollectionType);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCardCollection(cardCollection.getCardCollectionPK()) != null) {
                throw new PreexistingEntityException("CardCollection " + cardCollection + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardCollection cardCollection) throws NonexistentEntityException, Exception {
        cardCollection.getCardCollectionPK().setCardCollectionTypeId(cardCollection.getCardCollectionType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardCollection persistentCardCollection = em.find(CardCollection.class, cardCollection.getCardCollectionPK());
            CardCollectionType cardCollectionTypeOld = persistentCardCollection.getCardCollectionType();
            CardCollectionType cardCollectionTypeNew = cardCollection.getCardCollectionType();
            if (cardCollectionTypeNew != null) {
                cardCollectionTypeNew = em.getReference(cardCollectionTypeNew.getClass(), cardCollectionTypeNew.getId());
                cardCollection.setCardCollectionType(cardCollectionTypeNew);
            }
            cardCollection = em.merge(cardCollection);
            if (cardCollectionTypeOld != null && !cardCollectionTypeOld.equals(cardCollectionTypeNew)) {
                cardCollectionTypeOld.getCardCollectionList().remove(cardCollection);
                cardCollectionTypeOld = em.merge(cardCollectionTypeOld);
            }
            if (cardCollectionTypeNew != null && !cardCollectionTypeNew.equals(cardCollectionTypeOld)) {
                cardCollectionTypeNew.getCardCollectionList().add(cardCollection);
                cardCollectionTypeNew = em.merge(cardCollectionTypeNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CardCollectionPK id = cardCollection.getCardCollectionPK();
                if (findCardCollection(id) == null) {
                    throw new NonexistentEntityException("The cardCollection with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CardCollectionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardCollection cardCollection;
            try {
                cardCollection = em.getReference(CardCollection.class, id);
                cardCollection.getCardCollectionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardCollection with id " + id + " no longer exists.", enfe);
            }
            CardCollectionType cardCollectionType = cardCollection.getCardCollectionType();
            if (cardCollectionType != null) {
                cardCollectionType.getCardCollectionList().remove(cardCollection);
                cardCollectionType = em.merge(cardCollectionType);
            }
            em.remove(cardCollection);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardCollection> findCardCollectionEntities() {
        return findCardCollectionEntities(true, -1, -1);
    }

    public List<CardCollection> findCardCollectionEntities(int maxResults, int firstResult) {
        return findCardCollectionEntities(false, maxResults, firstResult);
    }

    private List<CardCollection> findCardCollectionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardCollection.class));
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

    public CardCollection findCardCollection(CardCollectionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardCollection.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardCollectionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardCollection> rt = cq.from(CardCollection.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
