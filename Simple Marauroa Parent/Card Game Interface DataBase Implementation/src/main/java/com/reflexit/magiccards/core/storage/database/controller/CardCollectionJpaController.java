/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reflexit.magiccards.core.storage.database.controller;

import java.io.Serializable;

import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.reflexit.magiccards.core.storage.database.CardCollectionType;
import com.reflexit.magiccards.core.storage.database.CardCollectionHasCard;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.reflexit.magiccards.core.storage.database.CardCollection;
import com.reflexit.magiccards.core.storage.database.CardCollectionPK;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.IllegalOrphanException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.NonexistentEntityException;
import com.reflexit.magiccards.core.storage.database.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CardCollectionJpaController implements Serializable
{
  public CardCollectionJpaController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager()
  {
    return emf.createEntityManager();
  }

  public void create(CardCollection cardCollection) throws PreexistingEntityException, Exception
  {
    if (cardCollection.getCardCollectionPK() == null)
    {
      cardCollection.setCardCollectionPK(new CardCollectionPK());
    }
    if (cardCollection.getCardCollectionHasCardList() == null)
    {
      cardCollection.setCardCollectionHasCardList(new ArrayList<CardCollectionHasCard>());
    }
    cardCollection.getCardCollectionPK().setCardCollectionTypeId(cardCollection.getCardCollectionType().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      CardCollectionType cardCollectionType = cardCollection.getCardCollectionType();
      if (cardCollectionType != null)
      {
        cardCollectionType = em.getReference(cardCollectionType.getClass(), cardCollectionType.getId());
        cardCollection.setCardCollectionType(cardCollectionType);
      }
      List<CardCollectionHasCard> attachedCardCollectionHasCardList = new ArrayList<CardCollectionHasCard>();
      for (CardCollectionHasCard cardCollectionHasCardListCardCollectionHasCardToAttach : cardCollection.getCardCollectionHasCardList())
      {
        cardCollectionHasCardListCardCollectionHasCardToAttach = em.getReference(cardCollectionHasCardListCardCollectionHasCardToAttach.getClass(), cardCollectionHasCardListCardCollectionHasCardToAttach.getCardCollectionHasCardPK());
        attachedCardCollectionHasCardList.add(cardCollectionHasCardListCardCollectionHasCardToAttach);
      }
      cardCollection.setCardCollectionHasCardList(attachedCardCollectionHasCardList);
      em.persist(cardCollection);
      if (cardCollectionType != null)
      {
        cardCollectionType.getCardCollectionList().add(cardCollection);
        cardCollectionType = em.merge(cardCollectionType);
      }
      for (CardCollectionHasCard cardCollectionHasCardListCardCollectionHasCard : cardCollection.getCardCollectionHasCardList())
      {
        CardCollection oldCardCollectionOfCardCollectionHasCardListCardCollectionHasCard = cardCollectionHasCardListCardCollectionHasCard.getCardCollection();
        cardCollectionHasCardListCardCollectionHasCard.setCardCollection(cardCollection);
        cardCollectionHasCardListCardCollectionHasCard = em.merge(cardCollectionHasCardListCardCollectionHasCard);
        if (oldCardCollectionOfCardCollectionHasCardListCardCollectionHasCard != null)
        {
          oldCardCollectionOfCardCollectionHasCardListCardCollectionHasCard.getCardCollectionHasCardList().remove(cardCollectionHasCardListCardCollectionHasCard);
          oldCardCollectionOfCardCollectionHasCardListCardCollectionHasCard = em.merge(oldCardCollectionOfCardCollectionHasCardListCardCollectionHasCard);
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findCardCollection(cardCollection.getCardCollectionPK()) != null)
      {
        throw new PreexistingEntityException("CardCollection " + cardCollection + " already exists.", ex);
      }
      throw ex;
    }
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }
  }

  public void edit(CardCollection cardCollection) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    cardCollection.getCardCollectionPK().setCardCollectionTypeId(cardCollection.getCardCollectionType().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      CardCollection persistentCardCollection = em.find(CardCollection.class, cardCollection.getCardCollectionPK());
      CardCollectionType cardCollectionTypeOld = persistentCardCollection.getCardCollectionType();
      CardCollectionType cardCollectionTypeNew = cardCollection.getCardCollectionType();
      List<CardCollectionHasCard> cardCollectionHasCardListOld = persistentCardCollection.getCardCollectionHasCardList();
      List<CardCollectionHasCard> cardCollectionHasCardListNew = cardCollection.getCardCollectionHasCardList();
      List<String> illegalOrphanMessages = null;
      for (CardCollectionHasCard cardCollectionHasCardListOldCardCollectionHasCard : cardCollectionHasCardListOld)
      {
        if (!cardCollectionHasCardListNew.contains(cardCollectionHasCardListOldCardCollectionHasCard))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<String>();
          }
          illegalOrphanMessages.add("You must retain CardCollectionHasCard " + cardCollectionHasCardListOldCardCollectionHasCard + " since its cardCollection field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      if (cardCollectionTypeNew != null)
      {
        cardCollectionTypeNew = em.getReference(cardCollectionTypeNew.getClass(), cardCollectionTypeNew.getId());
        cardCollection.setCardCollectionType(cardCollectionTypeNew);
      }
      List<CardCollectionHasCard> attachedCardCollectionHasCardListNew = new ArrayList<CardCollectionHasCard>();
      for (CardCollectionHasCard cardCollectionHasCardListNewCardCollectionHasCardToAttach : cardCollectionHasCardListNew)
      {
        cardCollectionHasCardListNewCardCollectionHasCardToAttach = em.getReference(cardCollectionHasCardListNewCardCollectionHasCardToAttach.getClass(), cardCollectionHasCardListNewCardCollectionHasCardToAttach.getCardCollectionHasCardPK());
        attachedCardCollectionHasCardListNew.add(cardCollectionHasCardListNewCardCollectionHasCardToAttach);
      }
      cardCollectionHasCardListNew = attachedCardCollectionHasCardListNew;
      cardCollection.setCardCollectionHasCardList(cardCollectionHasCardListNew);
      cardCollection = em.merge(cardCollection);
      if (cardCollectionTypeOld != null && !cardCollectionTypeOld.equals(cardCollectionTypeNew))
      {
        cardCollectionTypeOld.getCardCollectionList().remove(cardCollection);
        cardCollectionTypeOld = em.merge(cardCollectionTypeOld);
      }
      if (cardCollectionTypeNew != null && !cardCollectionTypeNew.equals(cardCollectionTypeOld))
      {
        cardCollectionTypeNew.getCardCollectionList().add(cardCollection);
        cardCollectionTypeNew = em.merge(cardCollectionTypeNew);
      }
      for (CardCollectionHasCard cardCollectionHasCardListNewCardCollectionHasCard : cardCollectionHasCardListNew)
      {
        if (!cardCollectionHasCardListOld.contains(cardCollectionHasCardListNewCardCollectionHasCard))
        {
          CardCollection oldCardCollectionOfCardCollectionHasCardListNewCardCollectionHasCard = cardCollectionHasCardListNewCardCollectionHasCard.getCardCollection();
          cardCollectionHasCardListNewCardCollectionHasCard.setCardCollection(cardCollection);
          cardCollectionHasCardListNewCardCollectionHasCard = em.merge(cardCollectionHasCardListNewCardCollectionHasCard);
          if (oldCardCollectionOfCardCollectionHasCardListNewCardCollectionHasCard != null && !oldCardCollectionOfCardCollectionHasCardListNewCardCollectionHasCard.equals(cardCollection))
          {
            oldCardCollectionOfCardCollectionHasCardListNewCardCollectionHasCard.getCardCollectionHasCardList().remove(cardCollectionHasCardListNewCardCollectionHasCard);
            oldCardCollectionOfCardCollectionHasCardListNewCardCollectionHasCard = em.merge(oldCardCollectionOfCardCollectionHasCardListNewCardCollectionHasCard);
          }
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        CardCollectionPK id = cardCollection.getCardCollectionPK();
        if (findCardCollection(id) == null)
        {
          throw new NonexistentEntityException("The cardCollection with id " + id + " no longer exists.");
        }
      }
      throw ex;
    }
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }
  }

  public void destroy(CardCollectionPK id) throws IllegalOrphanException, NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      CardCollection cardCollection;
      try
      {
        cardCollection = em.getReference(CardCollection.class, id);
        cardCollection.getCardCollectionPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The cardCollection with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<CardCollectionHasCard> cardCollectionHasCardListOrphanCheck = cardCollection.getCardCollectionHasCardList();
      for (CardCollectionHasCard cardCollectionHasCardListOrphanCheckCardCollectionHasCard : cardCollectionHasCardListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<String>();
        }
        illegalOrphanMessages.add("This CardCollection (" + cardCollection + ") cannot be destroyed since the CardCollectionHasCard " + cardCollectionHasCardListOrphanCheckCardCollectionHasCard + " in its cardCollectionHasCardList field has a non-nullable cardCollection field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      CardCollectionType cardCollectionType = cardCollection.getCardCollectionType();
      if (cardCollectionType != null)
      {
        cardCollectionType.getCardCollectionList().remove(cardCollection);
        cardCollectionType = em.merge(cardCollectionType);
      }
      em.remove(cardCollection);
      em.getTransaction().commit();
    }
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }
  }

  public List<CardCollection> findCardCollectionEntities()
  {
    return findCardCollectionEntities(true, -1, -1);
  }

  public List<CardCollection> findCardCollectionEntities(int maxResults, int firstResult)
  {
    return findCardCollectionEntities(false, maxResults, firstResult);
  }

  private List<CardCollection> findCardCollectionEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(CardCollection.class));
      Query q = em.createQuery(cq);
      if (!all)
      {
        q.setMaxResults(maxResults);
        q.setFirstResult(firstResult);
      }
      return q.getResultList();
    }
    finally
    {
      em.close();
    }
  }

  public CardCollection findCardCollection(CardCollectionPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(CardCollection.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getCardCollectionCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<CardCollection> rt = cq.from(CardCollection.class);
      cq.select(em.getCriteriaBuilder().count(rt));
      Query q = em.createQuery(cq);
      return ((Long) q.getSingleResult()).intValue();
    }
    finally
    {
      em.close();
    }
  }

}
