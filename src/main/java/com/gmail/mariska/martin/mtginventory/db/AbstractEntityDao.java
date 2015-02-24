package com.gmail.mariska.martin.mtginventory.db;

import javax.persistence.EntityManager;

public abstract class AbstractEntityDao<T> extends AbstractDao<T> implements IDao<T> {
    private final EntityManager em;
    private final Class<T> clazz;

    public AbstractEntityDao(Class<T> clazz, EntityManager em) {
        super(clazz, em);
        this.em = em;
        this.clazz = clazz;
    }

    public T findById(Long entityId) {
        return em.find(this.clazz, entityId);
    }

    @Override
    public T findById(String entityId) {
        return findById(Long.parseLong(entityId));
    }
}
