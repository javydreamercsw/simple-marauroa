package com.reflexit.magiccards.core.storage.database.controller;

import com.reflexit.magiccards.core.storage.database.CardAttribute;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.NonexistentEntityException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(cardAttribute);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCardAttribute(cardAttribute.getId()) != null) {
                throw new PreexistingEntityException("CardAttribute " + cardAttribute + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardAttribute cardAttribute) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cardAttribute = em.merge(cardAttribute);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cardAttribute.getId();
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

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardAttribute cardAttribute;
            try {
                cardAttribute = em.getReference(CardAttribute.class, id);
                cardAttribute.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardAttribute with id " + id + " no longer exists.", enfe);
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

    public CardAttribute findCardAttribute(Integer id) {
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
