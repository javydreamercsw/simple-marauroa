/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage.database.persistence.controller;

import dreamer.card.game.storage.database.persistence.Card;
import dreamer.card.game.storage.database.persistence.CardSet;
import dreamer.card.game.storage.database.persistence.CardSetPK;
import dreamer.card.game.storage.database.persistence.Game;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.ArrayList;
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
public class CardSetJpaController implements Serializable {

    public CardSetJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CardSet cardSet) throws PreexistingEntityException, Exception {
        if (cardSet.getCardSetPK() == null) {
            cardSet.setCardSetPK(new CardSetPK());
        }
        if (cardSet.getCardList() == null) {
            cardSet.setCardList(new ArrayList<Card>());
        }
        cardSet.getCardSetPK().setGameId(cardSet.getGame().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Game game = cardSet.getGame();
            if (game != null) {
                game = em.getReference(game.getClass(), game.getId());
                cardSet.setGame(game);
            }
            List<Card> attachedCardList = new ArrayList<Card>();
            for (Card cardListCardToAttach : cardSet.getCardList()) {
                cardListCardToAttach = em.getReference(cardListCardToAttach.getClass(), cardListCardToAttach.getCardPK());
                attachedCardList.add(cardListCardToAttach);
            }
            cardSet.setCardList(attachedCardList);
            em.persist(cardSet);
            if (game != null) {
                game.getCardSetList().add(cardSet);
                game = em.merge(game);
            }
            for (Card cardListCard : cardSet.getCardList()) {
                cardListCard.getCardSetList().add(cardSet);
                cardListCard = em.merge(cardListCard);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCardSet(cardSet.getCardSetPK()) != null) {
                throw new PreexistingEntityException("CardSet " + cardSet + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CardSet cardSet) throws NonexistentEntityException, Exception {
        cardSet.getCardSetPK().setGameId(cardSet.getGame().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardSet persistentCardSet = em.find(CardSet.class, cardSet.getCardSetPK());
            Game gameOld = persistentCardSet.getGame();
            Game gameNew = cardSet.getGame();
            List<Card> cardListOld = persistentCardSet.getCardList();
            List<Card> cardListNew = cardSet.getCardList();
            if (gameNew != null) {
                gameNew = em.getReference(gameNew.getClass(), gameNew.getId());
                cardSet.setGame(gameNew);
            }
            List<Card> attachedCardListNew = new ArrayList<Card>();
            for (Card cardListNewCardToAttach : cardListNew) {
                cardListNewCardToAttach = em.getReference(cardListNewCardToAttach.getClass(), cardListNewCardToAttach.getCardPK());
                attachedCardListNew.add(cardListNewCardToAttach);
            }
            cardListNew = attachedCardListNew;
            cardSet.setCardList(cardListNew);
            cardSet = em.merge(cardSet);
            if (gameOld != null && !gameOld.equals(gameNew)) {
                gameOld.getCardSetList().remove(cardSet);
                gameOld = em.merge(gameOld);
            }
            if (gameNew != null && !gameNew.equals(gameOld)) {
                gameNew.getCardSetList().add(cardSet);
                gameNew = em.merge(gameNew);
            }
            for (Card cardListOldCard : cardListOld) {
                if (!cardListNew.contains(cardListOldCard)) {
                    cardListOldCard.getCardSetList().remove(cardSet);
                    cardListOldCard = em.merge(cardListOldCard);
                }
            }
            for (Card cardListNewCard : cardListNew) {
                if (!cardListOld.contains(cardListNewCard)) {
                    cardListNewCard.getCardSetList().add(cardSet);
                    cardListNewCard = em.merge(cardListNewCard);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CardSetPK id = cardSet.getCardSetPK();
                if (findCardSet(id) == null) {
                    throw new NonexistentEntityException("The cardSet with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CardSetPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardSet cardSet;
            try {
                cardSet = em.getReference(CardSet.class, id);
                cardSet.getCardSetPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cardSet with id " + id + " no longer exists.", enfe);
            }
            Game game = cardSet.getGame();
            if (game != null) {
                game.getCardSetList().remove(cardSet);
                game = em.merge(game);
            }
            List<Card> cardList = cardSet.getCardList();
            for (Card cardListCard : cardList) {
                cardListCard.getCardSetList().remove(cardSet);
                cardListCard = em.merge(cardListCard);
            }
            em.remove(cardSet);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CardSet> findCardSetEntities() {
        return findCardSetEntities(true, -1, -1);
    }

    public List<CardSet> findCardSetEntities(int maxResults, int firstResult) {
        return findCardSetEntities(false, maxResults, firstResult);
    }

    private List<CardSet> findCardSetEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CardSet.class));
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

    public CardSet findCardSet(CardSetPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CardSet.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardSetCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CardSet> rt = cq.from(CardSet.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    private static final Logger LOG = Logger.getLogger(CardSetJpaController.class.getName());
    
}
