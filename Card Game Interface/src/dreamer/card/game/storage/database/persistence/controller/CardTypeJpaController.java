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
import dreamer.card.game.storage.database.persistence.Card;
import dreamer.card.game.storage.database.persistence.CardType;
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
public class CardTypeJpaController implements Serializable {

    public CardTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardType cardType) {
        if (cardType.getCardList() == null) {
            cardType.setCardList(new ArrayList<Card>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Card> attachedCardList = new ArrayList<Card>();
            for (Card cardListCardToAttach : cardType.getCardList()) {
                cardListCardToAttach = em.getReference(cardListCardToAttach.getClass(), cardListCardToAttach.getCardPK());
                attachedCardList.add(cardListCardToAttach);
            }
            cardType.setCardList(attachedCardList);
            em.persist(cardType);
            for (Card cardListCard : cardType.getCardList()) {
                CardType oldCardTypeOfCardListCard = cardListCard.getCardType();
                cardListCard.setCardType(cardType);
                cardListCard = em.merge(cardListCard);
                if (oldCardTypeOfCardListCard != null) {
                    oldCardTypeOfCardListCard.getCardList().remove(cardListCard);
                    oldCardTypeOfCardListCard = em.merge(oldCardTypeOfCardListCard);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardType cardType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardType persistentCardType = em.find(CardType.class, cardType.getId());
            List<Card> cardListOld = persistentCardType.getCardList();
            List<Card> cardListNew = cardType.getCardList();
            List<String> illegalOrphanMessages = null;
            for (Card cardListOldCard : cardListOld) {
                if (!cardListNew.contains(cardListOldCard)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Card " + cardListOldCard + " since its cardType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Card> attachedCardListNew = new ArrayList<Card>();
            for (Card cardListNewCardToAttach : cardListNew) {
                cardListNewCardToAttach = em.getReference(cardListNewCardToAttach.getClass(), cardListNewCardToAttach.getCardPK());
                attachedCardListNew.add(cardListNewCardToAttach);
            }
            cardListNew = attachedCardListNew;
            cardType.setCardList(cardListNew);
            cardType = em.merge(cardType);
            for (Card cardListNewCard : cardListNew) {
                if (!cardListOld.contains(cardListNewCard)) {
                    CardType oldCardTypeOfCardListNewCard = cardListNewCard.getCardType();
                    cardListNewCard.setCardType(cardType);
                    cardListNewCard = em.merge(cardListNewCard);
                    if (oldCardTypeOfCardListNewCard != null && !oldCardTypeOfCardListNewCard.equals(cardType)) {
                        oldCardTypeOfCardListNewCard.getCardList().remove(cardListNewCard);
                        oldCardTypeOfCardListNewCard = em.merge(oldCardTypeOfCardListNewCard);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cardType.getId();
                if (findCardType(id) == null) {
                    throw new NonexistentEntityException("The cardType with id " + id + " no longer exists.");
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
            CardType cardType;
            try {
                cardType = em.getReference(CardType.class, id);
                cardType.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Card> cardListOrphanCheck = cardType.getCardList();
            for (Card cardListOrphanCheckCard : cardListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CardType (" + cardType + ") cannot be destroyed since the Card " + cardListOrphanCheckCard + " in its cardList field has a non-nullable cardType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cardType);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardType> findCardTypeEntities() {
        return findCardTypeEntities(true, -1, -1);
    }

    public List<CardType> findCardTypeEntities(int maxResults, int firstResult) {
        return findCardTypeEntities(false, maxResults, firstResult);
    }

    private List<CardType> findCardTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardType.class));
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

    public CardType findCardType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardType.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardType> rt = cq.from(CardType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
