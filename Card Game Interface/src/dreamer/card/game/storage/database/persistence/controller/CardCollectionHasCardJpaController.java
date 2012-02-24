/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence.controller;

import dreamer.card.game.storage.database.persistence.Card;
import dreamer.card.game.storage.database.persistence.CardCollection;
import dreamer.card.game.storage.database.persistence.CardCollectionHasCard;
import dreamer.card.game.storage.database.persistence.CardCollectionHasCardPK;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
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
public class CardCollectionHasCardJpaController implements Serializable {

    public CardCollectionHasCardJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardCollectionHasCard cardCollectionHasCard) throws PreexistingEntityException, Exception {
        if (cardCollectionHasCard.getCardCollectionHasCardPK() == null) {
            cardCollectionHasCard.setCardCollectionHasCardPK(new CardCollectionHasCardPK());
        }
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardCollectionId(cardCollectionHasCard.getCardCollection().getCardCollectionPK().getId());
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardId(cardCollectionHasCard.getCard().getCardPK().getId());
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardCollectionCardCollectionTypeId(cardCollectionHasCard.getCardCollection().getCardCollectionPK().getCardCollectionTypeId());
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardCardTypeId(cardCollectionHasCard.getCard().getCardPK().getCardTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardCollection cardCollection = cardCollectionHasCard.getCardCollection();
            if (cardCollection != null) {
                cardCollection = em.getReference(cardCollection.getClass(), cardCollection.getCardCollectionPK());
                cardCollectionHasCard.setCardCollection(cardCollection);
            }
            Card card = cardCollectionHasCard.getCard();
            if (card != null) {
                card = em.getReference(card.getClass(), card.getCardPK());
                cardCollectionHasCard.setCard(card);
            }
            em.persist(cardCollectionHasCard);
            if (cardCollection != null) {
                cardCollection.getCardCollectionHasCardList().add(cardCollectionHasCard);
                cardCollection = em.merge(cardCollection);
            }
            if (card != null) {
                card.getCardCollectionHasCardList().add(cardCollectionHasCard);
                card = em.merge(card);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCardCollectionHasCard(cardCollectionHasCard.getCardCollectionHasCardPK()) != null) {
                throw new PreexistingEntityException("CardCollectionHasCard " + cardCollectionHasCard + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardCollectionHasCard cardCollectionHasCard) throws NonexistentEntityException, Exception {
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardCollectionId(cardCollectionHasCard.getCardCollection().getCardCollectionPK().getId());
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardId(cardCollectionHasCard.getCard().getCardPK().getId());
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardCollectionCardCollectionTypeId(cardCollectionHasCard.getCardCollection().getCardCollectionPK().getCardCollectionTypeId());
        cardCollectionHasCard.getCardCollectionHasCardPK().setCardCardTypeId(cardCollectionHasCard.getCard().getCardPK().getCardTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardCollectionHasCard persistentCardCollectionHasCard = em.find(CardCollectionHasCard.class, cardCollectionHasCard.getCardCollectionHasCardPK());
            CardCollection cardCollectionOld = persistentCardCollectionHasCard.getCardCollection();
            CardCollection cardCollectionNew = cardCollectionHasCard.getCardCollection();
            Card cardOld = persistentCardCollectionHasCard.getCard();
            Card cardNew = cardCollectionHasCard.getCard();
            if (cardCollectionNew != null) {
                cardCollectionNew = em.getReference(cardCollectionNew.getClass(), cardCollectionNew.getCardCollectionPK());
                cardCollectionHasCard.setCardCollection(cardCollectionNew);
            }
            if (cardNew != null) {
                cardNew = em.getReference(cardNew.getClass(), cardNew.getCardPK());
                cardCollectionHasCard.setCard(cardNew);
            }
            cardCollectionHasCard = em.merge(cardCollectionHasCard);
            if (cardCollectionOld != null && !cardCollectionOld.equals(cardCollectionNew)) {
                cardCollectionOld.getCardCollectionHasCardList().remove(cardCollectionHasCard);
                cardCollectionOld = em.merge(cardCollectionOld);
            }
            if (cardCollectionNew != null && !cardCollectionNew.equals(cardCollectionOld)) {
                cardCollectionNew.getCardCollectionHasCardList().add(cardCollectionHasCard);
                cardCollectionNew = em.merge(cardCollectionNew);
            }
            if (cardOld != null && !cardOld.equals(cardNew)) {
                cardOld.getCardCollectionHasCardList().remove(cardCollectionHasCard);
                cardOld = em.merge(cardOld);
            }
            if (cardNew != null && !cardNew.equals(cardOld)) {
                cardNew.getCardCollectionHasCardList().add(cardCollectionHasCard);
                cardNew = em.merge(cardNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CardCollectionHasCardPK id = cardCollectionHasCard.getCardCollectionHasCardPK();
                if (findCardCollectionHasCard(id) == null) {
                    throw new NonexistentEntityException("The cardCollectionHasCard with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CardCollectionHasCardPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardCollectionHasCard cardCollectionHasCard;
            try {
                cardCollectionHasCard = em.getReference(CardCollectionHasCard.class, id);
                cardCollectionHasCard.getCardCollectionHasCardPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardCollectionHasCard with id " + id + " no longer exists.", enfe);
            }
            CardCollection cardCollection = cardCollectionHasCard.getCardCollection();
            if (cardCollection != null) {
                cardCollection.getCardCollectionHasCardList().remove(cardCollectionHasCard);
                cardCollection = em.merge(cardCollection);
            }
            Card card = cardCollectionHasCard.getCard();
            if (card != null) {
                card.getCardCollectionHasCardList().remove(cardCollectionHasCard);
                card = em.merge(card);
            }
            em.remove(cardCollectionHasCard);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardCollectionHasCard> findCardCollectionHasCardEntities() {
        return findCardCollectionHasCardEntities(true, -1, -1);
    }

    public List<CardCollectionHasCard> findCardCollectionHasCardEntities(int maxResults, int firstResult) {
        return findCardCollectionHasCardEntities(false, maxResults, firstResult);
    }

    private List<CardCollectionHasCard> findCardCollectionHasCardEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardCollectionHasCard.class));
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

    public CardCollectionHasCard findCardCollectionHasCard(CardCollectionHasCardPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardCollectionHasCard.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardCollectionHasCardCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardCollectionHasCard> rt = cq.from(CardCollectionHasCard.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    private static final Logger LOG = Logger.getLogger(CardCollectionHasCardJpaController.class.getName());
    
}
