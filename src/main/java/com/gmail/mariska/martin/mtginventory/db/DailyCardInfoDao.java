package com.gmail.mariska.martin.mtginventory.db;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardShop;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.utils.Utils;

/**
 * Pro praci s informacemi o kartach
 * 
 * @author MAR
 */
public class DailyCardInfoDao extends AbstractEntityDao<DailyCardInfo> implements IDao<DailyCardInfo> {
    private EntityManager em;

    public DailyCardInfoDao(EntityManager em) {
        super(DailyCardInfo.class, em);
        this.em = em;
    }

    @Override
    public List<Order> getDefaultOrder(CriteriaBuilder cb, Root<DailyCardInfo> from) {
        return Arrays.asList(cb.desc(from.get(DailyCardInfo.PROPS.card.toString())),
                cb.desc(from.<CardShop> get(DailyCardInfo.PROPS.shop.toString())),
                cb.desc(from.<Date> get(DailyCardInfo.PROPS.day.toString())));
    }


    public List<DailyCardInfo> findByCardAndDay(Card c, Date day) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyCardInfo> q = cb.createQuery(DailyCardInfo.class);
        Root<DailyCardInfo> from = q.from(DailyCardInfo.class);
        q.select(from)
        .where(cb.and(cb.equal(from.<Card> get(DailyCardInfo.PROPS.card.toString()), c)),
                cb.equal(from.<Date> get(DailyCardInfo.PROPS.day.toString()), Utils.cutoffTime(day)));
        return em.createQuery(q).getResultList();
    }

    /**
     * informace o cenach danem vybranem okne
     * 
     * @param fisrtDate
     * @param secondDate
     * @return data Setridene podle card, shop, date
     */
    public List<DailyCardInfo> findByDates(Date fisrtDate, Date secondDate) {
// TypedQuery<DailyCardInfo> tq = em.createQuery("SELECT dci FROM DailyCardInfo dci "
// + "WHERE dci.day=:day1 OR dci.day=:day2 "
// + "ORDER BY dci.card DESC, dci.shop DESC, dci.day DESC", DailyCardInfo.class);
// tq.setParameter("day1", Utils.cutoffTime(fisrtDate));
// tq.setParameter("day2", Utils.cutoffTime(secondDate));
// // AND dci.card.name IS NOT NULL
// return tq.getResultList();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyCardInfo> q = cb.createQuery(DailyCardInfo.class);
        Root<DailyCardInfo> from = q.from(DailyCardInfo.class);
        q.select(from)
        .where(cb.or(
                cb.equal(from.<Date> get(DailyCardInfo.PROPS.day.toString()), Utils.cutoffTime(fisrtDate)),
                cb.equal(from.<Date> get(DailyCardInfo.PROPS.day.toString()), Utils.cutoffTime(secondDate))));
        q.orderBy(
                cb.desc(from.get(DailyCardInfo.PROPS.card.toString())),
                cb.desc(from.<CardShop> get(DailyCardInfo.PROPS.shop.toString())),
                cb.desc(from.<Date> get(DailyCardInfo.PROPS.day.toString()))
                );
        return em.createQuery(q).getResultList();
    }

    public List<DailyCardInfo> findByCard(Card c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyCardInfo> q = cb.createQuery(DailyCardInfo.class);
        Root<DailyCardInfo> from = q.from(DailyCardInfo.class);
        q.select(from)
        .where(cb.and(cb.equal(from.<Card> get(DailyCardInfo.PROPS.card.toString()), c)));
        q.orderBy(cb.asc(from.<Date> get(DailyCardInfo.PROPS.day.toString())),
                cb.asc(from.<CardShop> get(DailyCardInfo.PROPS.shop.toString())));
        return em.createQuery(q).getResultList();
    }

    public List<DailyCardInfo> findByCardOrderByShopDay(Card c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyCardInfo> q = cb.createQuery(DailyCardInfo.class);
        Root<DailyCardInfo> from = q.from(DailyCardInfo.class);
        q.select(from)
                .where(cb.and(cb.equal(from.<Card> get(DailyCardInfo.PROPS.card.toString()), c)));
        q.orderBy(cb.asc(from.<CardShop> get(DailyCardInfo.PROPS.shop.toString())), cb.asc(from.<Date> get(DailyCardInfo.PROPS.day.toString())));
        return em.createQuery(q).getResultList();
    }

    public List<DailyCardInfo> findByCardDayShop(Card c, Date day, CardShop shop) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailyCardInfo> q = cb.createQuery(DailyCardInfo.class);
        Root<DailyCardInfo> from = q.from(DailyCardInfo.class);
        q.select(from)
        .where(cb.and(cb.equal(from.<Card> get(DailyCardInfo.PROPS.card.toString()), c),
                cb.equal(from.<Date> get(DailyCardInfo.PROPS.day.toString()), Utils.cutoffTime(day)),
                cb.equal(from.<CardShop> get(DailyCardInfo.PROPS.shop.toString()), shop)));
        return em.createQuery(q).getResultList();
    }

    /**
     * Najde denni informace pro zadaneho uzivatele a dany den
     * 
     * @param userId
     * @param datum
     * @return
     */
    @SuppressWarnings("JpaQlInspection")
    public Collection<DailyCardInfo> findByUserCards(String userId, Date datum) {
        TypedQuery<DailyCardInfo> q = em.createQuery(
                "SELECT d FROM UsersCards uc, Card c, DailyCardInfo d "
                        + "WHERE c.id = uc.cardId AND d.card = c AND uc.userId = :userId AND d.day = :datum "
                        + "ORDER BY c.name, d.shop", DailyCardInfo.class);
        q.setParameter("userId", userId);
        q.setParameter("datum", Utils.cutoffTime(datum));
        return q.getResultList();
    }
}
