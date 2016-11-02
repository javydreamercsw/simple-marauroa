/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database.controller;

import com.reflexit.magiccards.core.storage.database.Card;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.reflexit.magiccards.core.storage.database.CardType;
import com.reflexit.magiccards.core.storage.database.CardSet;
import java.util.ArrayList;
import java.util.List;
import com.reflexit.magiccards.core.storage.database.CardCollectionHasCard;
import com.reflexit.magiccards.core.storage.database.CardHasCardAttribute;
import com.reflexit.magiccards.core.storage.database.CardPK;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.IllegalOrphanException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.NonexistentEntityException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class CardJpaController implements Serializable {

    public CardJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Card card) throws PreexistingEntityException, Exception {
        if (card.getCardPK() == null) {
            card.setCardPK(new CardPK());
        }
        if (card.getCardSetList() == null) {
            card.setCardSetList(new ArrayList<CardSet>());
        }
        if (card.getCardCollectionHasCardList() == null) {
            card.setCardCollectionHasCardList(new ArrayList<CardCollectionHasCard>());
        }
        if (card.getCardHasCardAttributeList() == null) {
            card.setCardHasCardAttributeList(new ArrayList<CardHasCardAttribute>());
        }
        card.getCardPK().setCardTypeId(card.getCardType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CardType cardType = card.getCardType();
            if (cardType != null) {
                cardType = em.getReference(cardType.getClass(), cardType.getId());
                card.setCardType(cardType);
            }
            List<CardSet> attachedCardSetList = new ArrayList<CardSet>();
            for (CardSet cardSetListCardSetToAttach : card.getCardSetList()) {
                cardSetListCardSetToAttach = em.getReference(cardSetListCardSetToAttach.getClass(), cardSetListCardSetToAttach.getCardSetPK());
                attachedCardSetList.add(cardSetListCardSetToAttach);
            }
            card.setCardSetList(attachedCardSetList);
            List<CardCollectionHasCard> attachedCardCollectionHasCardList = new ArrayList<CardCollectionHasCard>();
            for (CardCollectionHasCard cardCollectionHasCardListCardCollectionHasCardToAttach : card.getCardCollectionHasCardList()) {
                cardCollectionHasCardListCardCollectionHasCardToAttach = em.getReference(cardCollectionHasCardListCardCollectionHasCardToAttach.getClass(), cardCollectionHasCardListCardCollectionHasCardToAttach.getCardCollectionHasCardPK());
                attachedCardCollectionHasCardList.add(cardCollectionHasCardListCardCollectionHasCardToAttach);
            }
            card.setCardCollectionHasCardList(attachedCardCollectionHasCardList);
            List<CardHasCardAttribute> attachedCardHasCardAttributeList = new ArrayList<CardHasCardAttribute>();
            for (CardHasCardAttribute cardHasCardAttributeListCardHasCardAttributeToAttach : card.getCardHasCardAttributeList()) {
                cardHasCardAttributeListCardHasCardAttributeToAttach = em.getReference(cardHasCardAttributeListCardHasCardAttributeToAttach.getClass(), cardHasCardAttributeListCardHasCardAttributeToAttach.getCardHasCardAttributePK());
                attachedCardHasCardAttributeList.add(cardHasCardAttributeListCardHasCardAttributeToAttach);
            }
            card.setCardHasCardAttributeList(attachedCardHasCardAttributeList);
            em.persist(card);
            if (cardType != null) {
                cardType.getCardList().add(card);
                cardType = em.merge(cardType);
            }
            for (CardSet cardSetListCardSet : card.getCardSetList()) {
                cardSetListCardSet.getCardList().add(card);
                cardSetListCardSet = em.merge(cardSetListCardSet);
            }
            for (CardCollectionHasCard cardCollectionHasCardListCardCollectionHasCard : card.getCardCollectionHasCardList()) {
                Card oldCardOfCardCollectionHasCardListCardCollectionHasCard = cardCollectionHasCardListCardCollectionHasCard.getCard();
                cardCollectionHasCardListCardCollectionHasCard.setCard(card);
                cardCollectionHasCardListCardCollectionHasCard = em.merge(cardCollectionHasCardListCardCollectionHasCard);
                if (oldCardOfCardCollectionHasCardListCardCollectionHasCard != null) {
                    oldCardOfCardCollectionHasCardListCardCollectionHasCard.getCardCollectionHasCardList().remove(cardCollectionHasCardListCardCollectionHasCard);
                    oldCardOfCardCollectionHasCardListCardCollectionHasCard = em.merge(oldCardOfCardCollectionHasCardListCardCollectionHasCard);
                }
            }
            for (CardHasCardAttribute cardHasCardAttributeListCardHasCardAttribute : card.getCardHasCardAttributeList()) {
                Card oldCardOfCardHasCardAttributeListCardHasCardAttribute = cardHasCardAttributeListCardHasCardAttribute.getCard();
                cardHasCardAttributeListCardHasCardAttribute.setCard(card);
                cardHasCardAttributeListCardHasCardAttribute = em.merge(cardHasCardAttributeListCardHasCardAttribute);
                if (oldCardOfCardHasCardAttributeListCardHasCardAttribute != null) {
                    oldCardOfCardHasCardAttributeListCardHasCardAttribute.getCardHasCardAttributeList().remove(cardHasCardAttributeListCardHasCardAttribute);
                    oldCardOfCardHasCardAttributeListCardHasCardAttribute = em.merge(oldCardOfCardHasCardAttributeListCardHasCardAttribute);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCard(card.getCardPK()) != null) {
                throw new PreexistingEntityException("Card " + card + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Card card) throws IllegalOrphanException, NonexistentEntityException, Exception {
        card.getCardPK().setCardTypeId(card.getCardType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Card persistentCard = em.find(Card.class, card.getCardPK());
            CardType cardTypeOld = persistentCard.getCardType();
            CardType cardTypeNew = card.getCardType();
            List<CardSet> cardSetListOld = persistentCard.getCardSetList();
            List<CardSet> cardSetListNew = card.getCardSetList();
            List<CardCollectionHasCard> cardCollectionHasCardListOld = persistentCard.getCardCollectionHasCardList();
            List<CardCollectionHasCard> cardCollectionHasCardListNew = card.getCardCollectionHasCardList();
            List<CardHasCardAttribute> cardHasCardAttributeListOld = persistentCard.getCardHasCardAttributeList();
            List<CardHasCardAttribute> cardHasCardAttributeListNew = card.getCardHasCardAttributeList();
            List<String> illegalOrphanMessages = null;
            for (CardCollectionHasCard cardCollectionHasCardListOldCardCollectionHasCard : cardCollectionHasCardListOld) {
                if (!cardCollectionHasCardListNew.contains(cardCollectionHasCardListOldCardCollectionHasCard)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CardCollectionHasCard " + cardCollectionHasCardListOldCardCollectionHasCard + " since its card field is not nullable.");
                }
            }
            for (CardHasCardAttribute cardHasCardAttributeListOldCardHasCardAttribute : cardHasCardAttributeListOld) {
                if (!cardHasCardAttributeListNew.contains(cardHasCardAttributeListOldCardHasCardAttribute)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CardHasCardAttribute " + cardHasCardAttributeListOldCardHasCardAttribute + " since its card field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cardTypeNew != null) {
                cardTypeNew = em.getReference(cardTypeNew.getClass(), cardTypeNew.getId());
                card.setCardType(cardTypeNew);
            }
            List<CardSet> attachedCardSetListNew = new ArrayList<CardSet>();
            for (CardSet cardSetListNewCardSetToAttach : cardSetListNew) {
                cardSetListNewCardSetToAttach = em.getReference(cardSetListNewCardSetToAttach.getClass(), cardSetListNewCardSetToAttach.getCardSetPK());
                attachedCardSetListNew.add(cardSetListNewCardSetToAttach);
            }
            cardSetListNew = attachedCardSetListNew;
            card.setCardSetList(cardSetListNew);
            List<CardCollectionHasCard> attachedCardCollectionHasCardListNew = new ArrayList<CardCollectionHasCard>();
            for (CardCollectionHasCard cardCollectionHasCardListNewCardCollectionHasCardToAttach : cardCollectionHasCardListNew) {
                cardCollectionHasCardListNewCardCollectionHasCardToAttach = em.getReference(cardCollectionHasCardListNewCardCollectionHasCardToAttach.getClass(), cardCollectionHasCardListNewCardCollectionHasCardToAttach.getCardCollectionHasCardPK());
                attachedCardCollectionHasCardListNew.add(cardCollectionHasCardListNewCardCollectionHasCardToAttach);
            }
            cardCollectionHasCardListNew = attachedCardCollectionHasCardListNew;
            card.setCardCollectionHasCardList(cardCollectionHasCardListNew);
            List<CardHasCardAttribute> attachedCardHasCardAttributeListNew = new ArrayList<CardHasCardAttribute>();
            for (CardHasCardAttribute cardHasCardAttributeListNewCardHasCardAttributeToAttach : cardHasCardAttributeListNew) {
                cardHasCardAttributeListNewCardHasCardAttributeToAttach = em.getReference(cardHasCardAttributeListNewCardHasCardAttributeToAttach.getClass(), cardHasCardAttributeListNewCardHasCardAttributeToAttach.getCardHasCardAttributePK());
                attachedCardHasCardAttributeListNew.add(cardHasCardAttributeListNewCardHasCardAttributeToAttach);
            }
            cardHasCardAttributeListNew = attachedCardHasCardAttributeListNew;
            card.setCardHasCardAttributeList(cardHasCardAttributeListNew);
            card = em.merge(card);
            if (cardTypeOld != null && !cardTypeOld.equals(cardTypeNew)) {
                cardTypeOld.getCardList().remove(card);
                cardTypeOld = em.merge(cardTypeOld);
            }
            if (cardTypeNew != null && !cardTypeNew.equals(cardTypeOld)) {
                cardTypeNew.getCardList().add(card);
                cardTypeNew = em.merge(cardTypeNew);
            }
            for (CardSet cardSetListOldCardSet : cardSetListOld) {
                if (!cardSetListNew.contains(cardSetListOldCardSet)) {
                    cardSetListOldCardSet.getCardList().remove(card);
                    cardSetListOldCardSet = em.merge(cardSetListOldCardSet);
                }
            }
            for (CardSet cardSetListNewCardSet : cardSetListNew) {
                if (!cardSetListOld.contains(cardSetListNewCardSet)) {
                    cardSetListNewCardSet.getCardList().add(card);
                    cardSetListNewCardSet = em.merge(cardSetListNewCardSet);
                }
            }
            for (CardCollectionHasCard cardCollectionHasCardListNewCardCollectionHasCard : cardCollectionHasCardListNew) {
                if (!cardCollectionHasCardListOld.contains(cardCollectionHasCardListNewCardCollectionHasCard)) {
                    Card oldCardOfCardCollectionHasCardListNewCardCollectionHasCard = cardCollectionHasCardListNewCardCollectionHasCard.getCard();
                    cardCollectionHasCardListNewCardCollectionHasCard.setCard(card);
                    cardCollectionHasCardListNewCardCollectionHasCard = em.merge(cardCollectionHasCardListNewCardCollectionHasCard);
                    if (oldCardOfCardCollectionHasCardListNewCardCollectionHasCard != null && !oldCardOfCardCollectionHasCardListNewCardCollectionHasCard.equals(card)) {
                        oldCardOfCardCollectionHasCardListNewCardCollectionHasCard.getCardCollectionHasCardList().remove(cardCollectionHasCardListNewCardCollectionHasCard);
                        oldCardOfCardCollectionHasCardListNewCardCollectionHasCard = em.merge(oldCardOfCardCollectionHasCardListNewCardCollectionHasCard);
                    }
                }
            }
            for (CardHasCardAttribute cardHasCardAttributeListNewCardHasCardAttribute : cardHasCardAttributeListNew) {
                if (!cardHasCardAttributeListOld.contains(cardHasCardAttributeListNewCardHasCardAttribute)) {
                    Card oldCardOfCardHasCardAttributeListNewCardHasCardAttribute = cardHasCardAttributeListNewCardHasCardAttribute.getCard();
                    cardHasCardAttributeListNewCardHasCardAttribute.setCard(card);
                    cardHasCardAttributeListNewCardHasCardAttribute = em.merge(cardHasCardAttributeListNewCardHasCardAttribute);
                    if (oldCardOfCardHasCardAttributeListNewCardHasCardAttribute != null && !oldCardOfCardHasCardAttributeListNewCardHasCardAttribute.equals(card)) {
                        oldCardOfCardHasCardAttributeListNewCardHasCardAttribute.getCardHasCardAttributeList().remove(cardHasCardAttributeListNewCardHasCardAttribute);
                        oldCardOfCardHasCardAttributeListNewCardHasCardAttribute = em.merge(oldCardOfCardHasCardAttributeListNewCardHasCardAttribute);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CardPK id = card.getCardPK();
                if (findCard(id) == null) {
                    throw new NonexistentEntityException("The card with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CardPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Card card;
            try {
                card = em.getReference(Card.class, id);
                card.getCardPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The card with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CardCollectionHasCard> cardCollectionHasCardListOrphanCheck = card.getCardCollectionHasCardList();
            for (CardCollectionHasCard cardCollectionHasCardListOrphanCheckCardCollectionHasCard : cardCollectionHasCardListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Card (" + card + ") cannot be destroyed since the CardCollectionHasCard " + cardCollectionHasCardListOrphanCheckCardCollectionHasCard + " in its cardCollectionHasCardList field has a non-nullable card field.");
            }
            List<CardHasCardAttribute> cardHasCardAttributeListOrphanCheck = card.getCardHasCardAttributeList();
            for (CardHasCardAttribute cardHasCardAttributeListOrphanCheckCardHasCardAttribute : cardHasCardAttributeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Card (" + card + ") cannot be destroyed since the CardHasCardAttribute " + cardHasCardAttributeListOrphanCheckCardHasCardAttribute + " in its cardHasCardAttributeList field has a non-nullable card field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            CardType cardType = card.getCardType();
            if (cardType != null) {
                cardType.getCardList().remove(card);
                cardType = em.merge(cardType);
            }
            List<CardSet> cardSetList = card.getCardSetList();
            for (CardSet cardSetListCardSet : cardSetList) {
                cardSetListCardSet.getCardList().remove(card);
                cardSetListCardSet = em.merge(cardSetListCardSet);
            }
            em.remove(card);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Card> findCardEntities() {
        return findCardEntities(true, -1, -1);
    }

    public List<Card> findCardEntities(int maxResults, int firstResult) {
        return findCardEntities(false, maxResults, firstResult);
    }

    private List<Card> findCardEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Card.class));
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

    public Card findCard(CardPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Card.class, id);
        } finally {
            em.close();
        }
    }

    public int getCardCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Card> rt = cq.from(Card.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
