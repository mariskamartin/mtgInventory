package com.gmail.mariska.martin.mtginventory.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.gmail.mariska.martin.mtginventory.db.IDao;

public class AbstractService<T> {
    private EntityManager em;
    private IDao<T> dao;

    public AbstractService(EntityManager em, IDao<T> dao) {
        this.em = em;
        this.dao = dao;
    }

    public T insert(T newEntity) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        dao.insert(newEntity);
        tx.commit();
        return newEntity;
    }

    public T update(T entity) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        entity = dao.update(entity);
        tx.commit();
        return entity;
    }

    public T delete(String entityId) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        T entity = dao.findById(entityId);
        dao.delete(entity);
        tx.commit();
        return entity;
    }

    public List<T> getAll() {
        return dao.getAll();
    }

    public T findById(String entityId) {
        return dao.findById(entityId);
    }

    public IDao<T> getDao() {
        return dao;
    }

    public EntityManager getEm() {
        return em;
    }
}
