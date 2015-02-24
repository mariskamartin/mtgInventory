package com.gmail.mariska.martin.mtginventory.db;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

public interface IDao<T> {
    List<T> getAll();

    T findById(String id);

    void insert(T entity);

    T update(T entity);

    void delete(T entity);

    List<Order> getDefaultOrder(CriteriaBuilder cb, Root<T> from);

    IDao<T> limit(int maxRows);

    IDao<T> startAt(int i);
}
