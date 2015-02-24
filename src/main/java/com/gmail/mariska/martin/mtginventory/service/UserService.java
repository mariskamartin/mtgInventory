package com.gmail.mariska.martin.mtginventory.service;

import javax.persistence.EntityManager;

import com.gmail.mariska.martin.mtginventory.db.IDao;
import com.gmail.mariska.martin.mtginventory.db.UserDao;
import com.gmail.mariska.martin.mtginventory.db.model.User;

/**
 * Bussines logika pro Uzivatele
 * 
 * @author MAR
 */
public class UserService extends AbstractService<User> {
    private UserDao userDao;

// private UserCardsDao usersCardsDao;

    protected UserService(EntityManager em, IDao<User> dao) {
        super(em, dao);
        // TODO prijde refaktrovat uplne ven. Service nebude zodpovedne za vyrobeni DAO.. muze pracovat s ruznym dao
        this.userDao = (UserDao) dao;
        // this.usersCardsDao = new UserCardsDao(em);
    }

    public User findByToken(String token) {
        return userDao.findByToken(token);
    }
}
