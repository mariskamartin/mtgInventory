package com.gmail.mariska.martin.mtginventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import com.gmail.mariska.martin.mtginventory.db.model.*;
import com.gmail.mariska.martin.mtginventory.utils.Utils;
import com.gmail.mariska.martin.objectdb.OdbTestSuite;
import org.joda.time.DateTime;
import org.junit.*;

import com.gmail.mariska.martin.mtginventory.db.BannedCardNamesDao;
import com.gmail.mariska.martin.mtginventory.db.CardDao;
import com.gmail.mariska.martin.mtginventory.db.DailyCardInfoDao;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public class CardServiceTest extends OdbTestSuite {


    @Test
    public void testMergeDciWithNewCard() throws Exception {
        Date created = DateTime.now().minusDays(2).toDate();
        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardService cardService = new CardService(em, null, null, new CardDao(em), new BannedCardNamesDao(em));

            Card c1 = createCard("NEWCARD", CardEdition.FATE_REFORGED, CardRarity.RARE, created);
            DailyCardInfo dailyCardInfo = new DailyCardInfo(c1, BigDecimal.valueOf(20.0), 2, DateTime.now().toDate(), CardShop.TOLARIE);

            Collection<Card> cards = cardService.saveCardsIntoDb(Arrays.asList(dailyCardInfo), true);

            Card saved = cards.iterator().next();
            assertTrue(saved.getCreated() != null);
            Card cardFromDb = cardService.findById(saved.getId());
            assertTrue(cardFromDb.getCreated() != null);
        }
        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardService cardService = new CardService(em, null, null, new CardDao(em), new BannedCardNamesDao(em));

            Card c1 = createCard("NEWCARD", CardEdition.FATE_REFORGED, CardRarity.RARE);
            DailyCardInfo dailyCardInfo = new DailyCardInfo(c1, BigDecimal.valueOf(19.0), 1, DateTime.now().toDate(), CardShop.CERNY_RYTIR);

            Collection<Card> cards = cardService.saveCardsIntoDb(Arrays.asList(dailyCardInfo), true);

            //created time should stay as first
            Card saved = cards.iterator().next();
            assertTrue(saved.getCreated().getTime() == Utils.cutoffTime(created).getTime());
            Card cardFromDb = cardService.findById(saved.getId());
            assertTrue(cardFromDb.getCreated().getTime() == Utils.cutoffTime(created).getTime());
        }
    }

    @Test
    public void testGenerateCardsMovements() throws Exception {
        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardService cardService = new CardService(em, null, null, new CardDao(em), new BannedCardNamesDao(em));
            setUpDb(em, cardService);
            cardService.generateCardsMovements(new Date(), CardMovementType.DAY);
            List<CardMovement> movts = cardService.getCardMovementsByType(CardMovementType.DAY);
            assertEquals("5.0", movts.get(0).getGainPrice().toString());
        }
    }

    @Test
    public void testSaveDailyInfoCards() throws Exception {
        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardService cardService = new CardService(em, null, null, new CardDao(em), new BannedCardNamesDao(em));
            setUpDb(em, cardService);
            List<DailyCardInfo> list = new ArrayList<>();

            Card c1 = createCard("Test", CardEdition.KHANS_OF_TARKIR, CardRarity.COMMON);
            Card c2 = createCard("Test", CardEdition.KHANS_OF_TARKIR, CardRarity.COMMON);
            Card c3 = createCard("Test", CardEdition.KHANS_OF_TARKIR, CardRarity.COMMON);

            list.add(new DailyCardInfo(c1, BigDecimal.valueOf(20.0), 2, new Date(), CardShop.CERNY_RYTIR));
            list.add(new DailyCardInfo(c2, BigDecimal.valueOf(25.0), 4, new Date(), CardShop.TOLARIE));
            list.add(new DailyCardInfo(c3, BigDecimal.valueOf(14.0), 1, new Date(), CardShop.NAJADA));

            Collection<Card> cards = cardService.saveCardsIntoDb(list, true);
            assertEquals(1, cards.size());
        }
    }

    @Test(expected = RollbackException.class)
    public void testFailOnMergingNotUniqueCards() {
        Card c1 = CardFactory.createFableHeroAutoId();
        Card c2 = CardFactory.createFableHeroAutoId();

        try (OdbTestSuite.ClosableEM em = OdbTestSuite.getClosableEM()) {
            em.getTransaction().begin();
            c1 = em.merge(c1); // vraci managed objekt
            c2 = em.merge(c2); // vraci managed objekt
            em.getTransaction().commit();
        }

        assertTrue(true);
    }

    @Test
    public void test2() {
        Card c2 = CardFactory.createFableHero();
        DailyCardInfo dci1 = new DailyCardInfo(c2, new BigDecimal(20), 5, new Date(), CardShop.TOLARIE);


        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            DailyCardInfoDao dao = new DailyCardInfoDao(em);
            em.getTransaction().begin();
            dci1 = dao.update(dci1);
            em.getTransaction().commit();
        }

        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardDao dao = new CardDao(em);
            Card find = dao.findById(c2.getId());
            assertEquals(c2.getName(), find.getName());
        }
    }

    @Test
    public void testName() throws Exception {
        EventBus eventBus = mock(EventBus.class);
        WebPageSnifferService sniffer = mock(WebPageSnifferService.class);

        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            em.getTransaction().begin();
            new CardDao(em).insert(CardFactory.create("TestInDB",CardEdition.ANTIQUITIES,CardRarity.COMMON));
            em.getTransaction().commit();
        }

        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardService cardService = new CardService(em, eventBus, sniffer, new CardDao(em), new BannedCardNamesDao(em));
            List<DailyCardInfo> cardList = Lists.newArrayList();

            Card c3 = CardFactory.create("Test2",CardEdition.ALLIANCES,CardRarity.COMMON);
            DailyCardInfo dci3 = new DailyCardInfo(c3, new BigDecimal(150), 3, new Date(), CardShop.NAJADA);
            Card c4 = CardFactory.create("Test2",CardEdition.ALLIANCES,CardRarity.COMMON);
            DailyCardInfo dci4 = new DailyCardInfo(c4, new BigDecimal(10), 5, new Date(), CardShop.TOLARIE);
            Card c2 = CardFactory.create("TestInDB",CardEdition.ANTIQUITIES,CardRarity.COMMON);
            DailyCardInfo dci5 = new DailyCardInfo(c2, new BigDecimal(5), 1, new Date(), CardShop.TOLARIE);

            cardList.add(dci3);
            cardList.add(dci4);
            cardList.add(dci5);
            Collection<Card> cards = cardService.saveCardsIntoDb(cardList, true);
        }
        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardDao dao = new CardDao(em);
            DailyCardInfoDao daoDci = new DailyCardInfoDao(em);
            assertEquals(CardEdition.ALLIANCES, dao.findByName("Test2", true).get(0).getEdition());
            assertEquals(1, daoDci.findByCard(dao.findByName("TestInDB", true).get(0)).get(0).getStoreAmount());
        }

    }


    private Card createCard(String name, CardEdition edition, CardRarity rarity, Date created) {
        Card card = createCard(name, edition, rarity);
        card.setCreated(created);
        return card;
    }

    private Card createCard(String name, CardEdition edition, CardRarity rarity) {
        Card c2 = new Card();
        c2.setName(name);
        c2.setEdition(edition);
        c2.setRarity(rarity);
        return c2;
    }

    private void setUpDb(EntityManager em, CardService cardService) {
        List<DailyCardInfo> list = new ArrayList<>();

        Card c1 = createCard("A", CardEdition.EDITION_10TH, CardRarity.COMMON);

        Date date = new DateTime().minusDays(1).toDate();
        list.add(new DailyCardInfo(c1, BigDecimal.valueOf(20.0), 2, date, CardShop.CERNY_RYTIR));
        list.add(new DailyCardInfo(c1, BigDecimal.valueOf(25.0), 2, new Date(), CardShop.CERNY_RYTIR));

        cardService.saveCardsIntoDb(list, true);
    }
}
