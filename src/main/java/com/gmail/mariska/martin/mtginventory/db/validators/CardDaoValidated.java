package com.gmail.mariska.martin.mtginventory.db.validators;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import com.gmail.mariska.martin.mtginventory.db.BannedCardNamesDao;
import com.gmail.mariska.martin.mtginventory.db.CardDao;
import com.gmail.mariska.martin.mtginventory.db.model.BannedCardNames;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.google.common.collect.Sets;

/**
 * Validator okolo CardDao.. s dalsi logikou ukladani.
 * 
 * TODO bude lepsi udelat uplen Validator vedle.. ktery dostane kartu atd.. a zvaliduje to tam kde pouze chce servisa..
 * 
 * @author MAR
 * 
 */
public class CardDaoValidated extends CardDao {
    private Set<String>  bannedCardNameSet = Sets.newHashSet();

    public CardDaoValidated(EntityManager em, BannedCardNamesDao bannedDao) {
        super(em);
        List<BannedCardNames> all = bannedDao.getAll();
        for (BannedCardNames bc : all) {
            bannedCardNameSet.add(bc.getIdBannedName());
        }
        System.out.println("###############################");
        System.out.println(bannedCardNameSet.toString());
    }

    @Override
    public void insert(Card entity) {
        // kontorola na banovane karty
        if (bannedCardNameSet.contains(entity.getName())) {
            throw new BannedCardException();
        }
        super.insert(entity);
    }
}
