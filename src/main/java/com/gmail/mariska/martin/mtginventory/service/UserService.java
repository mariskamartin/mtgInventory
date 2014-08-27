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
//    private UserCardsDao usersCardsDao;

    public UserService(EntityManager em) {
        super(em, new UserDao(em));
//        this.usersCardsDao = new UserCardsDao(em);
    }

}
