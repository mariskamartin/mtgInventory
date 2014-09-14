package com.gmail.mariska.martin.mtginventory.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.BannedCardNamesDao;
import com.gmail.mariska.martin.mtginventory.db.CardDao;
import com.gmail.mariska.martin.mtginventory.db.CardMovementDao;
import com.gmail.mariska.martin.mtginventory.db.DailyCardInfoDao;
import com.gmail.mariska.martin.mtginventory.db.model.BannedCardNames;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovement;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovementType;
import com.gmail.mariska.martin.mtginventory.db.model.CardRarity;
import com.gmail.mariska.martin.mtginventory.db.model.CardShop;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.db.validators.BannedCardException;
import com.gmail.mariska.martin.mtginventory.db.validators.CardDaoValidated;
import com.gmail.mariska.martin.mtginventory.listeners.DatabaseManager;
import com.gmail.mariska.martin.mtginventory.listeners.EventBusManager;
import com.gmail.mariska.martin.mtginventory.listeners.SupportServiciesManager;
import com.gmail.mariska.martin.mtginventory.service.AlertService.DailyCardInfoAlertEvent;
import com.gmail.mariska.martin.mtginventory.service.AlertService.MovementAlertEvent;
import com.gmail.mariska.martin.mtginventory.utils.Utils;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

/**
 * Bussines logika pro Karty
 * 
 * @author MAR
 */
public class CardService extends AbstractService<Card> {
    private static final Logger logger = Logger.getLogger(CardService.class);

    private CardDao cardDao;
    private EventBus eventBus; // pro postovani zprav
    private WebPageSnifferService webPageSnifferService;
    private BannedCardNamesDao bannedCardNamesDao;


    public CardService(EntityManager em, EventBus eventBus, WebPageSnifferService sniffer) {
        super(em, new CardDaoValidated(em, new BannedCardNamesDao(em)));
        this.cardDao = (CardDao) super.getDao();
        this.bannedCardNamesDao = new BannedCardNamesDao(em);
        this.eventBus = eventBus;
        this.webPageSnifferService = sniffer;
    }

    public CardService(ServletContext ctx) {
        this(DatabaseManager.getEM(ctx),
                EventBusManager.getEventBus(ctx),
                new WebPageSnifferService(SupportServiciesManager.getExecutorService(ctx)));
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
     * Dohleda zadany text v nazvech karet
     * 
     * @param cardName
     */
    public Collection<Card> findsCards(String cardName) {
        Preconditions.checkArgument(cardName.length() > 1, "Musi se zadat aspon 2 znaky.");
        return cardDao.findByName(cardName, false, false);
    }

    /**
     * Odchyti karty z webu a zalozi aktualni informace do db, pouze pokud ten den se tak jeste nestalo
     * 
     * @param cardName
     * @param rarity
     * @param edition
     */
    public Collection<Card> fetchCardsByEditionRarityOnCR(CardEdition edition, String rarity) {
        try {
            return saveCardsIntoDb(webPageSnifferService.findCardsAtCRByEditionAndRarity(edition, rarity), null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    /**
     * Odchyti vsechny aktualne spravovane karty
     * 
     * @return
     */
    public Collection<Card> fetchAllManagedCards() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> cardNames = this.cardDao.getAllCardsNames();
        long namesProcessedCount = 0;
        List<Card> allCards = new ArrayList<>();
        List<DailyCardInfo> addedDailyCardInformations = new ArrayList<>();
        List<List<String>> partitionedCardNames = Lists.partition(cardNames, 10);
        for (List<String> cardNamelist : partitionedCardNames) {
            String[] cardNamesArray = cardNamelist.toArray(new String[cardNamelist.size()]);
            allCards.addAll(saveCardsIntoDb(fetchCardListByName(cardNamesArray),addedDailyCardInformations));
            namesProcessedCount += cardNamelist.size();
            if (namesProcessedCount % 25 == 0) {
                logger.info("fetching procesed " + namesProcessedCount + "/" + cardNames.size());
            }
        }

        logger.info("all cards fetched " + namesProcessedCount + "/" + cardNames.size());
        logger.info("elapsed time " + stopwatch.stop().elapsed(TimeUnit.MINUTES) + " minutes");
        // post new detached dci
        postNewCardDailyInfo(addedDailyCardInformations);
        return allCards;
    }

    /**
     * Odchyti karty z webu a zalozi aktualni informace do db, pouze pokud ten den se tak jeste nestalo
     * 
     * @param cardName
     */
    public Collection<Card> fetchCards(String cardName) {
        Preconditions.checkArgument(cardName.length() > 3, "Název zadané karty musí být delší než 3 znaky");
        return saveCardsIntoDb(fetchCardListByName(cardName), null);
    }

    private ImmutableList<DailyCardInfo> fetchCardListByName(String... cardNames) {
        try {
            return webPageSnifferService.findCardsAtWeb(cardNames);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ImmutableList.of();
    }

    private Collection<Card> saveCardsIntoDb(ImmutableList<DailyCardInfo> cardList,
            List<DailyCardInfo> addedDailyCardInformations) {
        DailyCardInfoDao dciDao = new DailyCardInfoDao(getEm());
        EntityTransaction tx = getEm().getTransaction();
        Map<String, Card> managedCardsMap = new HashMap<String, Card>();
        try {
            tx.begin();
            for (DailyCardInfo dailyCardInfo : cardList) {
                Card c = dailyCardInfo.getCard();
                // pokud je karta nezname edice nebo rarity, tak neukladame
                if (c.getRarity().equals(CardRarity.UNKNOWN) || c.getEdition().equals(CardEdition.UNKNOWN)) {
                    continue;
                }

                if (managedCardsMap.containsKey(getCardKey(c))) {
                    c = managedCardsMap.get(getCardKey(c));
                } else {
                    List<Card> findList = cardDao.findByNameEditionRarityFoil(c.getName(), c.getEdition(),
                            c.getRarity(), c.isFoil());
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
                List<DailyCardInfo> findInfoList = dciDao.findByCardDayShop(dailyCardInfo.getCard(),
                        dailyCardInfo.getDay(), dailyCardInfo.getShop());
                if (findInfoList.isEmpty()) {
                    dciDao.insert(dailyCardInfo);
                    if (addedDailyCardInformations != null) {
                        addedDailyCardInformations.add(dailyCardInfo);
                    }
                } else if (findInfoList.size() == 1) {
                    DailyCardInfo dciInDb = findInfoList.get(0);
                    if (Utils.hasChange(dciInDb, dailyCardInfo, DailyCardInfo.PROPS.price.toString(),
                            DailyCardInfo.PROPS.storeAmount.toString())) {
                        dciInDb.setPrice(dailyCardInfo.getPrice());
                        dciInDb.setStoreAmount(dailyCardInfo.getStoreAmount());
                        dciDao.update(dciInDb);
                        if (addedDailyCardInformations != null) {
                            addedDailyCardInformations.add(dciInDb);
                        }
                    }
                } else {
                    logger.warn("Dohledano vic nez jedno DailyInfoData pro: " + dailyCardInfo + " // "
                            + dailyCardInfo.getCard());
                }
            }
            tx.commit();
        } catch (BannedCardException e) {
            if (logger.isTraceEnabled()) {
                logger.trace(e.getMessage());
            }
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return managedCardsMap.values();
    }

    private String getCardKey(Card c) {
        return c.getName() + c.getEdition() + c.getRarity() + c.isFoil();
    }

    /**
     * Vygeneruje pro zadany den pohyby cen karet
     * 
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
                // stejna karta - stejny shop > spocitat
                if (!newerDci.getPrice().equals(dailyCardInfo.getPrice())) {
                    EntityTransaction tx = em.getTransaction();
                    try {
                        tx.begin();
                        CardMovement cm = new CardMovement(lastCard, movementType, newerDci.getShop(), now,
                                dailyCardInfo.getPrice(), newerDci.getPrice());
                        postNewMovement(cm);
                        cmDao.insert(cm);
                        tx.commit();
                    } catch (Exception e) {
                        if (tx.isActive()) {
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

    private void postNewCardDailyInfo(List<DailyCardInfo> addedInformations) {
        if (eventBus != null) {
            eventBus.post(new DailyCardInfoAlertEvent(addedInformations));
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
     * 
     * @param type
     * @return
     */
    public List<CardMovement> getCardMovementsByType(CardMovementType type) {
        EntityManager em = getEm();
        CardMovementDao cmDao = new CardMovementDao(em);
        return cmDao.getByType(type);
    }

    /**
     * Zabanuje jmeno dane karty
     * 
     * @param bannedCard
     * @return
     */
    public BannedCardNames addBannedCardName(BannedCardNames bannedCard) {
        getEm().getTransaction().begin();
        bannedCardNamesDao.insert(bannedCard);
        getEm().getTransaction().commit();
        return bannedCard;
    }
}
