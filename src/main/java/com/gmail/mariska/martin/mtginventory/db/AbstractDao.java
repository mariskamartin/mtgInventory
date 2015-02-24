package com.gmail.mariska.martin.mtginventory.db;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

public abstract class AbstractDao<T> implements IDao<T> {
    private EntityManager em;
    private Class<T> clazz;
    private int maxResults = -1;
    private int startAtRow = -1;

    public AbstractDao(Class<T> clazz, EntityManager em) {
        this.clazz = clazz;
        this.em = em;
    }

    @Override
    public List<T> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(this.clazz);
        Root<T> from = q.from(this.clazz);
        TypedQuery<T> query = em.createQuery(q.select(from).orderBy(getDefaultOrder(cb, from)));
        if (maxResults >= 0) {
            query.setMaxResults(maxResults);
        }
        if (startAtRow >= 0) {
            query.setFirstResult(startAtRow);
        }
        return query.getResultList();
    }

    @Override
    public List<Order> getDefaultOrder(CriteriaBuilder cb, Root<T> from) {
        return Collections.emptyList();
    }

    @Override
    public void insert(T entity) {
        em.persist(entity);
    }

    @Override
    public T update(T entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(T entity) {
        em.remove(entity);
    }

    @Override
    public T findById(String entityId) {
        return em.find(this.clazz, entityId);
    }

    @Override
    public IDao<T> limit(int maxRows) {
        maxResults = maxRows;
        return this;
    }

    @Override
    public IDao<T> startAt(int indexOfRow) {
        this.startAtRow = indexOfRow;
        return this;
    }
}
