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
import com.reflexit.magiccards.core.storage.database.CardSet;
import com.reflexit.magiccards.core.storage.database.Game;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.IllegalOrphanException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class GameJpaController implements Serializable {

    public GameJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Game game) {
        if (game.getCardSetList() == null) {
            game.setCardSetList(new ArrayList<CardSet>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CardSet> attachedCardSetList = new ArrayList<CardSet>();
            for (CardSet cardSetListCardSetToAttach : game.getCardSetList()) {
                cardSetListCardSetToAttach = em.getReference(cardSetListCardSetToAttach.getClass(), cardSetListCardSetToAttach.getCardSetPK());
                attachedCardSetList.add(cardSetListCardSetToAttach);
            }
            game.setCardSetList(attachedCardSetList);
            em.persist(game);
            for (CardSet cardSetListCardSet : game.getCardSetList()) {
                Game oldGameOfCardSetListCardSet = cardSetListCardSet.getGame();
                cardSetListCardSet.setGame(game);
                cardSetListCardSet = em.merge(cardSetListCardSet);
                if (oldGameOfCardSetListCardSet != null) {
                    oldGameOfCardSetListCardSet.getCardSetList().remove(cardSetListCardSet);
                    oldGameOfCardSetListCardSet = em.merge(oldGameOfCardSetListCardSet);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Game game) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Game persistentGame = em.find(Game.class, game.getId());
            List<CardSet> cardSetListOld = persistentGame.getCardSetList();
            List<CardSet> cardSetListNew = game.getCardSetList();
            List<String> illegalOrphanMessages = null;
            for (CardSet cardSetListOldCardSet : cardSetListOld) {
                if (!cardSetListNew.contains(cardSetListOldCardSet)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CardSet " + cardSetListOldCardSet + " since its game field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CardSet> attachedCardSetListNew = new ArrayList<CardSet>();
            for (CardSet cardSetListNewCardSetToAttach : cardSetListNew) {
                cardSetListNewCardSetToAttach = em.getReference(cardSetListNewCardSetToAttach.getClass(), cardSetListNewCardSetToAttach.getCardSetPK());
                attachedCardSetListNew.add(cardSetListNewCardSetToAttach);
            }
            cardSetListNew = attachedCardSetListNew;
            game.setCardSetList(cardSetListNew);
            game = em.merge(game);
            for (CardSet cardSetListNewCardSet : cardSetListNew) {
                if (!cardSetListOld.contains(cardSetListNewCardSet)) {
                    Game oldGameOfCardSetListNewCardSet = cardSetListNewCardSet.getGame();
                    cardSetListNewCardSet.setGame(game);
                    cardSetListNewCardSet = em.merge(cardSetListNewCardSet);
                    if (oldGameOfCardSetListNewCardSet != null && !oldGameOfCardSetListNewCardSet.equals(game)) {
                        oldGameOfCardSetListNewCardSet.getCardSetList().remove(cardSetListNewCardSet);
                        oldGameOfCardSetListNewCardSet = em.merge(oldGameOfCardSetListNewCardSet);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = game.getId();
                if (findGame(id) == null) {
                    throw new NonexistentEntityException("The game with id " + id + " no longer exists.");
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
            Game game;
            try {
                game = em.getReference(Game.class, id);
                game.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The game with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CardSet> cardSetListOrphanCheck = game.getCardSetList();
            for (CardSet cardSetListOrphanCheckCardSet : cardSetListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Game (" + game + ") cannot be destroyed since the CardSet " + cardSetListOrphanCheckCardSet + " in its cardSetList field has a non-nullable game field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(game);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Game> findGameEntities() {
        return findGameEntities(true, -1, -1);
    }

    public List<Game> findGameEntities(int maxResults, int firstResult) {
        return findGameEntities(false, maxResults, firstResult);
    }

    private List<Game> findGameEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Game.class));
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

    public Game findGame(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Game.class, id);
        } finally {
            em.close();
        }
    }

    public int getGameCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Game> rt = cq.from(Game.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
