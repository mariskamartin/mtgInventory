package com.gmail.mariska.martin.mtginventory.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.gmail.mariska.martin.mtginventory.db.model.CardMovement;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovementType;
import com.gmail.mariska.martin.mtginventory.utils.Utils;

/**
 * Pro praci s pohyby cen karet
 * 
 * @author MAR
 * 
 */
public class CardMovementDao extends AbstractDao<CardMovement> implements IDao<CardMovement> {
    private EntityManager em;

    public CardMovementDao(EntityManager em) {
        super(CardMovement.class, em);
        this.em = em;
    }

    public List<CardMovement> getByType(CardMovementType type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CardMovement> q = cb.createQuery(CardMovement.class);
        Root<CardMovement> from = q.from(CardMovement.class);
        return em.createQuery(q.select(from)
                .where(cb.equal(from.<CardMovementType> get(CardMovement.PROPS.type.toString()), type))
                .orderBy(getDefaultOrder(cb, from)))
                .getResultList();
    }

    @Override
    public List<Order> getDefaultOrder(CriteriaBuilder cb, Root<CardMovement> from) {
        return Arrays.asList(cb.desc(from.get(CardMovement.PROPS.day.toString())),
                cb.desc(from.get(CardMovement.PROPS.gainPercentage.toString())));
    }

    /**
     * Smaze vsechny pohyby cen a vrati pocet smazanych
     * 
     * @return
     */
    public int deleteAllByType(CardMovementType type) {
        Query q = em.createQuery("DELETE FROM CardMovement cm WHERE cm.type = :cmType");
        q.setParameter("cmType", type);
        return q.executeUpdate();
    }

    /**
     * Dohleda pro zadany den pohyby nad karami daneho uzivatele
     * 
     * @param userId
     * @param datum
     * @return
     */
    public Collection<CardMovement> findByUserCards(String userId, Date datum) {
        TypedQuery<CardMovement> q = em.createQuery(
                "SELECT m FROM UsersCards uc, Card c, CardMovement m "
                        + "WHERE c.id = uc.cardId AND m.card = c AND uc.userId=:userId AND m.created=:datum AND m.type=:moveType",
                        CardMovement.class);
        q.setParameter("userId", userId);
        q.setParameter("moveType", CardMovementType.DAY);
        q.setParameter("datum", Utils.cutoffTime(datum));
        return q.getResultList();
    }

}
