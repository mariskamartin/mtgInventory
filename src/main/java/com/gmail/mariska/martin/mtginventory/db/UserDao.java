package com.gmail.mariska.martin.mtginventory.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.gmail.mariska.martin.mtginventory.db.model.User;

/**
 * Pro praci s uzivatelem
 * @author MAR
 *
 */
public class UserDao extends AbstractDao<User> implements IDao<User> {
    private EntityManager em;

    public UserDao(EntityManager em) {
        super(User.class, em);
        this.em = em;
    }

    public User findByToken(String token) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> q = cb.createQuery(User.class);
        Root<User> from = q.from(User.class);
        List<User> resultList = em.createQuery(q.select(from).where(cb.equal(from.get(User.PROPS.token.toString()), token)).orderBy(getDefaultOrder(cb, from))).getResultList();
        return resultList.size() == 0 ? null : resultList.get(0);
    }
}
