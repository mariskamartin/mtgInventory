package com.gmail.mariska.martin.mtginventory.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.gmail.mariska.martin.mtginventory.db.model.UsersCards;

/**
 * Pro praci s uzivatelovymi kartami
 * 
 * @author MAR
 * 
 */
public class UserCardsDao extends AbstractDao<UsersCards> implements IDao<UsersCards> {
    private EntityManager em;

    public UserCardsDao(EntityManager em) {
        super(UsersCards.class, em);
        this.em = em;
    }

    public List<UsersCards> getAllByUserId(String userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsersCards> q = cb.createQuery(UsersCards.class);
        // FROM Variable Paths:
        Root<UsersCards> from = q.from(UsersCards.class);

        return em.createQuery(q.select(from)
                .where(cb.equal(from.get("userId"), userId))
                .orderBy(getDefaultOrder(cb, from)))
                .getResultList();
    }
}
