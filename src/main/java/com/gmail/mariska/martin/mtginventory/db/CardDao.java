package com.gmail.mariska.martin.mtginventory.db;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardRarity;
import com.gmail.mariska.martin.mtginventory.utils.Utils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Pro praci s kartami
 * 
 * @author MAR
 * 
 */
public class CardDao extends AbstractDao<Card> implements IDao<Card> {
    private EntityManager em;

    public CardDao(EntityManager em) {
        super(Card.class, em);
        this.em = em;
    }

    @Override
    public void insert(Card entity) {
        super.insert(entity);
    }

    public List<Card> getAllWithoutFoil() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Card> q = cb.createQuery(Card.class);
        Root<Card> from = q.from(Card.class);
        return em.createQuery(q.select(from)
                .where(cb.equal(from.get(Card.PROPS.foil.toString()), false))
                .orderBy(getDefaultOrder(cb, from)))
                .getResultList();
    }

    @Override
    public List<Order> getDefaultOrder(CriteriaBuilder cb, Root<Card> from) {
        return Arrays.asList(cb.asc(from.get(Card.PROPS.name.toString())));
    }

    public List<Card> findByNameEditionRarityFoil(String name, CardEdition edition, CardRarity cardRarity, boolean foil) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Card> q = cb.createQuery(Card.class);
        Root<Card> from = q.from(Card.class);
        q.select(from).where(
                cb.and(cb.equal(from.get(Card.PROPS.name.toString()), name), cb.equal(from.get(Card.PROPS.edition.toString()), edition),
                        cb.equal(from.get(Card.PROPS.rarity.toString()), cardRarity), cb.equal(from.get(Card.PROPS.foil.toString()), foil)));
        return em.createQuery(q).getResultList();
    }

    public List<Card> findByEdition(CardEdition edition) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Card> q = cb.createQuery(Card.class);
        Root<Card> from = q.from(Card.class);
        q.select(from).where(cb.equal(from.get(Card.PROPS.edition.toString()), edition));
        return em.createQuery(q).getResultList();
    }

    public List<Card> findByRarity(CardRarity rarity) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Card> q = cb.createQuery(Card.class);
        Root<Card> from = q.from(Card.class);
        q.select(from).where(cb.equal(from.get(Card.PROPS.rarity.toString()), rarity));
        return em.createQuery(q).getResultList();
    }

    public List<Card> findByName(String name, boolean exact, boolean foil) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Card> q = cb.createQuery(Card.class);
        Root<Card> from = q.from(Card.class);
        q.select(from)
        .where(cb.and(cb.like(cb.lower(from.<String> get(Card.PROPS.name.toString())), exact ? name.toLowerCase() : "%" + name.toLowerCase() + "%"),
                cb.equal(from.get(Card.PROPS.foil.toString()), foil)))
                .orderBy(getDefaultOrder(cb, from));
        return em.createQuery(q).getResultList();
    }

    public List<Card> findByCreatedDate(Date date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Card> q = cb.createQuery(Card.class);
        Root<Card> from = q.from(Card.class);
        q.select(from).where(cb.equal(from.<Date> get(Card.PROPS.created.toString()), Utils.cutoffTime(date)));
        return em.createQuery(q).getResultList();
    }

    public List<String> getAllCardsNames() {
        CriteriaQuery<Tuple> cq = em.getCriteriaBuilder().createTupleQuery();
        Root<Card> from = cq.from(Card.class);
        cq.multiselect(from.get(Card.PROPS.name.toString())).distinct(true);
        List<Tuple> tupleResult = em.createQuery(cq).getResultList();
        return Lists.transform(tupleResult, new Function<Tuple, String>() {
            @Override
            public String apply(Tuple tuple) {
                return (String) tuple.get(0);
            }
        });
    }

    public List<Card> findByUser(String userId) {
        TypedQuery<Card> cardQuery = em.createQuery("SELECT c FROM Card c, UsersCards uc WHERE uc.userId = :userId AND uc.cardId = c.id", Card.class);
        cardQuery.setParameter("userId", userId);
        return cardQuery.getResultList();
    }

}
