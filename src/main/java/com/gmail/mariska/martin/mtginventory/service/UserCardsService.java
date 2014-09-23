package com.gmail.mariska.martin.mtginventory.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.gmail.mariska.martin.mtginventory.db.IDao;
import com.gmail.mariska.martin.mtginventory.db.UserCardsDao;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.User;
import com.gmail.mariska.martin.mtginventory.db.model.UsersCards;
import com.google.common.collect.Lists;

/**
 * Bussines logika pro Uzivatele
 * 
 * @author MAR
 */
public class UserCardsService extends AbstractService<UsersCards> {
    private UserCardsDao usersCardsDao;

    protected UserCardsService(EntityManager em, IDao<UsersCards> dao) {
        super(em, dao);
        this.usersCardsDao = (UserCardsDao) dao;
    }

    public List<Card> getAllUsersCards(String userId) {
        List<UsersCards> userCards = usersCardsDao.getAllByUserId(userId);
        ArrayList<Card> cards = Lists.newArrayList();
        for (UsersCards usersCards : userCards) {
            cards.add(usersCards.getCard());
        }
        return cards;
    }

    public UsersCards addUserCard(String userId, String cardId) {
        EntityManager em = getEm();
        User user = em.find(User.class, userId);
        Card findCard = em.find(Card.class, cardId);
        UsersCards usersCards = new UsersCards();
        usersCards.setUser(user);
        usersCards.setCard(findCard);
        usersCards.setWatched(true);
        em.getTransaction().begin();
        usersCardsDao.insert(usersCards);
        em.getTransaction().commit();
        return usersCards;
    }
}
