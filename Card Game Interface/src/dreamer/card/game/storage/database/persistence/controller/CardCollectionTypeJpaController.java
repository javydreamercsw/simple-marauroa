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
import dreamer.card.game.storage.database.persistence.CardCollection;
import dreamer.card.game.storage.database.persistence.CardCollectionType;
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
public class CardCollectionTypeJpaController implements Serializable {

    public CardCollectionTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardCollectionType cardCollectionType) {
        if (cardCollectionType.getCardCollectionList() == null) {
            cardCollectionType.setCardCollectionList(new ArrayList<CardCollection>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CardCollection> attachedCardCollectionList = new ArrayList<CardCollection>();
            for (CardCollection cardCollectionListCardCollectionToAttach : cardCollectionType.getCardCollectionList()) {
                cardCollectionListCardCollectionToAttach = em.getReference(cardCollectionListCardCollectionToAttach.getClass(), cardCollectionListCardCollectionToAttach.getCardCollectionPK());
                attachedCardCollectionList.add(cardCollectionListCardCollectionToAttach);
            }
            cardCollectionType.setCardCollectionList(attachedCardCollectionList);
            em.persist(cardCollectionType);
            for (CardCollection cardCollectionListCardCollection : cardCollectionType.getCardCollectionList()) {
                CardCollectionType oldCardCollectionTypeOfCardCollectionListCardCollection = cardCollectionListCardCollection.getCardCollectionType();
                cardCollectionListCardCollection.setCardCollectionType(cardCollectionType);
                cardCollectionListCardCollection = em.merge(cardCollectionListCardCollection);
                if (oldCardCollectionTypeOfCardCollectionListCardCollection != null) {
                    oldCardCollectionTypeOfCardCollectionListCardCollection.getCardCollectionList().remove(cardCollectionListCardCollection);
                    oldCardCollectionTypeOfCardCollectionListCardCollection = em.merge(oldCardCollectionTypeOfCardCollectionListCardCollection);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardCollectionType cardCollectionType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardCollectionType persistentCardCollectionType = em.find(CardCollectionType.class, cardCollectionType.getId());
            List<CardCollection> cardCollectionListOld = persistentCardCollectionType.getCardCollectionList();
            List<CardCollection> cardCollectionListNew = cardCollectionType.getCardCollectionList();
            List<String> illegalOrphanMessages = null;
            for (CardCollection cardCollectionListOldCardCollection : cardCollectionListOld) {
                if (!cardCollectionListNew.contains(cardCollectionListOldCardCollection)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CardCollection " + cardCollectionListOldCardCollection + " since its cardCollectionType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CardCollection> attachedCardCollectionListNew = new ArrayList<CardCollection>();
            for (CardCollection cardCollectionListNewCardCollectionToAttach : cardCollectionListNew) {
                cardCollectionListNewCardCollectionToAttach = em.getReference(cardCollectionListNewCardCollectionToAttach.getClass(), cardCollectionListNewCardCollectionToAttach.getCardCollectionPK());
                attachedCardCollectionListNew.add(cardCollectionListNewCardCollectionToAttach);
            }
            cardCollectionListNew = attachedCardCollectionListNew;
            cardCollectionType.setCardCollectionList(cardCollectionListNew);
            cardCollectionType = em.merge(cardCollectionType);
            for (CardCollection cardCollectionListNewCardCollection : cardCollectionListNew) {
                if (!cardCollectionListOld.contains(cardCollectionListNewCardCollection)) {
                    CardCollectionType oldCardCollectionTypeOfCardCollectionListNewCardCollection = cardCollectionListNewCardCollection.getCardCollectionType();
                    cardCollectionListNewCardCollection.setCardCollectionType(cardCollectionType);
                    cardCollectionListNewCardCollection = em.merge(cardCollectionListNewCardCollection);
                    if (oldCardCollectionTypeOfCardCollectionListNewCardCollection != null && !oldCardCollectionTypeOfCardCollectionListNewCardCollection.equals(cardCollectionType)) {
                        oldCardCollectionTypeOfCardCollectionListNewCardCollection.getCardCollectionList().remove(cardCollectionListNewCardCollection);
                        oldCardCollectionTypeOfCardCollectionListNewCardCollection = em.merge(oldCardCollectionTypeOfCardCollectionListNewCardCollection);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cardCollectionType.getId();
                if (findCardCollectionType(id) == null) {
                    throw new NonexistentEntityException("The cardCollectionType with id " + id + " no longer exists.");
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
            CardCollectionType cardCollectionType;
            try {
                cardCollectionType = em.getReference(CardCollectionType.class, id);
                cardCollectionType.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardCollectionType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CardCollection> cardCollectionListOrphanCheck = cardCollectionType.getCardCollectionList();
            for (CardCollection cardCollectionListOrphanCheckCardCollection : cardCollectionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CardCollectionType (" + cardCollectionType + ") cannot be destroyed since the CardCollection " + cardCollectionListOrphanCheckCardCollection + " in its cardCollectionList field has a non-nullable cardCollectionType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cardCollectionType);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardCollectionType> findCardCollectionTypeEntities() {
        return findCardCollectionTypeEntities(true, -1, -1);
    }

    public List<CardCollectionType> findCardCollectionTypeEntities(int maxResults, int firstResult) {
        return findCardCollectionTypeEntities(false, maxResults, firstResult);
    }

    private List<CardCollectionType> findCardCollectionTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardCollectionType.class));
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

    public CardCollectionType findCardCollectionType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardCollectionType.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardCollectionTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardCollectionType> rt = cq.from(CardCollectionType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
