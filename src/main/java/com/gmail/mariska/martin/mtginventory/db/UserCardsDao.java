package com.gmail.mariska.martin.mtginventory.db;

import javax.persistence.EntityManager;

import com.gmail.mariska.martin.mtginventory.db.model.UsersCards;

/**
 * Pro praci s uzivatelovymi kartami
 * 
 * @author MAR
 * 
 */
public class UserCardsDao extends AbstractDao<UsersCards> implements IDao<UsersCards> {
    public UserCardsDao(EntityManager em) {
        super(UsersCards.class, em);
    }
}
