package com.gmail.mariska.martin.mtginventory.db;

import javax.persistence.EntityManager;

import com.gmail.mariska.martin.mtginventory.db.model.BannedCardNames;

/**
 * Pro praci se zabanovanymi kartamy
 * @author MAR
 *
 */
public class BannedCardNamesDao extends AbstractDao<BannedCardNames> implements IDao<BannedCardNames> {

    public BannedCardNamesDao(EntityManager em) {
        super(BannedCardNames.class, em);
    }
}
