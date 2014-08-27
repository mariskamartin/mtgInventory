package com.gmail.mariska.martin.mtginventory.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.CardDao;
import com.gmail.mariska.martin.mtginventory.db.CardMovementDao;
import com.gmail.mariska.martin.mtginventory.db.DailyCardInfoDao;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovement;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovementType;
import com.gmail.mariska.martin.mtginventory.db.model.CardShop;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.service.AlertService.MovementAlertEvent;
import com.gmail.mariska.martin.mtginventory.service.CardService.CardEvent;
import com.gmail.mariska.martin.mtginventory.utils.observer.AbstractObservedSubject;
import com.gmail.mariska.martin.mtginventory.utils.observer.IObservedSubject;
import com.gmail.mariska.martin.mtginventory.utils.observer.IObserver;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

/**
 * Bussines logika pro Karty
 * 
 * @author MAR
 */
public class CardService extends AbstractService<Card> implements IObservedSubject<CardEvent> {
    private static final Logger logger = Logger.getLogger(CardService.class);

    private AbstractObservedSubject<CardEvent> internSubject = new AbstractObservedSubject<CardEvent>() {};
    private CardDao cardDao;
    private EventBus eventBus; //pro postovani zprav

    public CardService(EntityManager em) {
        super(em, new CardDao(em));
        this.cardDao = (CardDao) super.getDao();
    }

    public CardService(EntityManager em, EventBus eventBus) {
        this(em);
        this.eventBus = eventBus;
    }

    /**
     * Pri smazani karty musime vymazat i navazane informace pro danou kartu.
     */
    @Override
    public Card delete(String entityId) {
        EntityManager em = getEm();
        DailyCardInfoDao dciDao = new DailyCardInfoDao(em);
        Card card = this.findById(entityId);
        List<DailyCardInfo> dciList = dciDao.findByCard(card);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (DailyCardInfo dci : dciList) {
            dciDao.delete(dci);
        }
        cardDao.delete(card);
        tx.commit();
        return card;
    }

    public List<Card> getAllWithoutFoil() {
        return cardDao.getAllWithoutFoil();
    }

    public List<DailyCardInfo> getDailyInfo(String id) {
        DailyCardInfoDao dciDao = new DailyCardInfoDao(getEm());
        Card card = getDao().findById(id);
        return dciDao.findByCard(card);
    }

    /**
     * Odchyti vsechny aktualne spravovane karty
     * @return
     */
    public Collection<Card> fetchAllManagedCards() {
        List<String> cardNames = this.cardDao.getAllCardsNames();
        List<Card> allCards = new ArrayList<>();
        for (String name : cardNames) {
            allCards.addAll(fetchCards(name));
        }
        return allCards;
    }

    /**
     * Odchyti karty z webu a zalozi aktualni informace do db, pouze pokud ten den se tak jeste nestalo
     * @param cardName
     */
    public Collection<Card> fetchCards(String cardName) {
        WebPageSnifferService snifferService = new WebPageSnifferService();
        DailyCardInfoDao dciDao = new DailyCardInfoDao(getEm());
        EntityTransaction tx = getEm().getTransaction();
        Map<String, Card> managedCardsMap = new HashMap<String, Card>();
        try {
            ImmutableList<DailyCardInfo> cardList = snifferService.findCardsAtWeb(cardName);
            tx.begin();
            for (DailyCardInfo dailyCardInfo : cardList) {
                Card c = dailyCardInfo.getCard();
                if (managedCardsMap.containsKey(getCardKey(c))) {
                    c = managedCardsMap.get(getCardKey(c));
                } else {
                    List<Card> findList = cardDao.findByNameEditionRarityFoil(c.getName(), c.getEdition(), c.getRarity(), c.isFoil());
                    if (findList.isEmpty()) {
                        cardDao.insert(c);
                        managedCardsMap.put(getCardKey(c), c);
                        if (logger.isDebugEnabled()) {
                            logger.debug("new managed card added: " + c);
                        }
                    } else if (findList.size() > 1) {
                        throw new IllegalStateException("Nalezeno vice karet pro jeden nazev");
                    } else {
                        c = findList.get(0);
                        managedCardsMap.put(getCardKey(c), c);
                    }
                }

                dailyCardInfo.setCard(c); // priradit managed entitu
                List<DailyCardInfo> findInfoList = dciDao.findByCardDayShop(dailyCardInfo.getCard(), dailyCardInfo.getDay(), dailyCardInfo.getShop());
                if (findInfoList.isEmpty()) {
                    dciDao.insert(dailyCardInfo);
                } else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Jiz jsou dnesni DailyInfoData ulozeny.");
                    }
                }
            }
            tx.commit();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return managedCardsMap.values();
    }

    private String getCardKey(Card c) {
        return c.getName()+c.getEdition()+c.getRarity()+c.isFoil();
    }

    /**
     * Vygeneruje pro zadany den pohyby cen karet
     * @param date
     */
    public void generateCardsMovements(Date now, CardMovementType movementType) {
        EntityManager em = getEm();
        DailyCardInfoDao dciDao = new DailyCardInfoDao(em);
        CardMovementDao cmDao = new CardMovementDao(em);

        Date dayInPast = movementType.getDayFrom(now);
        List<DailyCardInfo> findedDates = dciDao.findByDates(dayInPast, now);

        Card lastCard = new Card();
        DailyCardInfo newerDci = new DailyCardInfo();
        CardShop lastShop = CardShop.UNKNOWN;

        for (DailyCardInfo dailyCardInfo : findedDates) {
            if (lastCard.equals(dailyCardInfo.getCard()) && lastShop.equals(dailyCardInfo.getShop())) {
                //stejna karta - stejny shop > spocitat
                if (!newerDci.getPrice().equals(dailyCardInfo.getPrice())) {
                    EntityTransaction tx = em.getTransaction();
                    try {
                        tx.begin();
                        CardMovement cm = new CardMovement(lastCard, movementType, newerDci.getShop(), now, dailyCardInfo.getPrice(), newerDci.getPrice());
                        postNewMovement(cm);
                        cmDao.insert(cm);
                        tx.commit();
                    } catch (Exception e) {
                        if(tx.isActive()){
                            tx.rollback();
                        }
                    }
                }
            }
            newerDci = dailyCardInfo;
            lastCard = dailyCardInfo.getCard();
            lastShop = dailyCardInfo.getShop();
        }

    }

    private void postNewMovement(CardMovement cm) {
        if (eventBus != null) {
            eventBus.post(new MovementAlertEvent(cm));
        }
    }

    public void deleteCardMovementByType(CardMovementType movementType) {
        EntityManager em = getEm();
        CardMovementDao cmDao = new CardMovementDao(em);
        EntityTransaction txd = em.getTransaction();
        txd.begin();
        try {
            cmDao.deleteAllByType(movementType);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        txd.commit();
    }

    /**
     * Vrati pohyby karet podle zadaneho typu pohybu.
     * @param type
     * @return
     */
    public List<CardMovement> getCardMovementsByType(CardMovementType type) {
        EntityManager em = getEm();
        CardMovementDao cmDao = new CardMovementDao(em);
        return cmDao.getByType(type);
    }

    @Override
    public void addObserver(IObserver<CardEvent> o) {
        internSubject.addObserver(o);
    }

    @Override
    public void removeObserver(IObserver<CardEvent> o) {
        internSubject.removeObserver(o);
    }

    @Override
    public void notifyObservers(CardEvent event) {
        internSubject.notifyObservers(event);
    }

    /**
     * Slouze pro observery
     * 
     * @author MAR
     */
    public static class CardEvent {

    }
}
