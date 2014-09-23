package com.gmail.mariska.martin.mtginventory.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.gmail.mariska.martin.mtginventory.db.IDao;
import com.gmail.mariska.martin.mtginventory.db.UserCardsDao;
import com.gmail.mariska.martin.mtginventory.db.model.UsersCards;

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

    /**
     * Nacte vsechny vazby uzivatel-karta podle userId
     * @param userId
     * @return
     */
    public List<UsersCards> getAllUsersCards(String userId) {
        return usersCardsDao.getAllByUserId(userId);
    }

    /**
     * Vlozi pro zadaneho uzivatele, zadanou kartu jako sledovanou
     * @param userId
     * @param cardId
     * @return
     */
    public UsersCards addUserCard(String userId, String cardId) {
        EntityManager em = getEm();
        UsersCards usersCards = new UsersCards();
        usersCards.setUserId(userId);
        usersCards.setCardId(cardId);
        usersCards.setWatched(true);
        em.getTransaction().begin();
        usersCardsDao.insert(usersCards);
        em.getTransaction().commit();
        return usersCards;
    }
}
