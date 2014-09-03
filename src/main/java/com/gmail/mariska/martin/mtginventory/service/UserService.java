package com.gmail.mariska.martin.mtginventory.service;

import javax.persistence.EntityManager;

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

    public UserService(EntityManager em) {
        super(em, new UserDao(em));
        // TODO prijde refaktrovat uplne ven. Service nebude zodpovedne za vyrobeni DAO.. muze pracovat s ruznym dao
        this.userDao = (UserDao) getDao();
        // this.usersCardsDao = new UserCardsDao(em);
    }

    public User findByToken(String token) {
        return userDao.findByToken(token);
    }
}
