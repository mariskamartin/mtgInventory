package com.gmail.mariska.martin.mtginventory.db;

import javax.persistence.EntityManager;

import com.gmail.mariska.martin.mtginventory.db.model.User;

/**
 * Pro praci s uzivatelem
 * @author MAR
 *
 */
public class UserDao extends AbstractDao<User> implements IDao<User> {
    public UserDao(EntityManager em) {
        super(User.class, em);
    }
}
