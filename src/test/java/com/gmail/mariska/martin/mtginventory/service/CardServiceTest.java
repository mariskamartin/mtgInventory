package com.gmail.mariska.martin.mtginventory.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.RollbackException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.gmail.mariska.martin.mtginventory.db.BannedCardNamesDao;
import com.gmail.mariska.martin.mtginventory.db.CardDao;
import com.gmail.mariska.martin.mtginventory.db.DailyCardInfoDao;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardFactory;
import com.gmail.mariska.martin.mtginventory.db.model.CardRarity;
import com.gmail.mariska.martin.mtginventory.db.model.CardShop;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.objectdb.test.OdbTestSuite;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public class CardServiceTest extends OdbTestSuite {

    @Test(expected = RollbackException.class)
    public void testFailOnMergingNotUniqueCards() {
        Card c1 = CardFactory.createFableHeroAutoId();
        Card c2 = CardFactory.createFableHeroAutoId();

        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
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
            Assert.assertEquals(c2.getName(), find.getName());
        }
    }

    @Test
    @Ignore
    public void test3() {
        Card c2 = CardFactory.create("Test",CardEdition.ALPHA,CardRarity.COMMON);
        DailyCardInfo dci1 = new DailyCardInfo(c2, new BigDecimal(20), 5, new Date(), CardShop.TOLARIE);
        DailyCardInfo dci2 = new DailyCardInfo(c2, new BigDecimal(20), 2, new Date(), CardShop.NAJADA);
        Card c3 = CardFactory.create("Test",CardEdition.ALPHA,CardRarity.COMMON);
        DailyCardInfo dci3 = new DailyCardInfo(c3, new BigDecimal(150), 3, new Date(), CardShop.NAJADA);

        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            em.getTransaction().begin();
            new CardDao(em).insert(c2);
            em.getTransaction().commit();
        }

        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            DailyCardInfoDao dao = new DailyCardInfoDao(em);
            em.getTransaction().begin();
            dci1 = dao.update(dci1);
            dci2 = dao.update(dci2);
            try {
                dci3 = dao.update(dci3);
                em.getTransaction().commit();
            } catch (RollbackException e) {
                if (e.getMessage().contains("Unique constraint")) {
                    em.getTransaction().begin();
                    CardDao cdao = new CardDao(em);
                    List<Card> findByNameEditionRarityFoil = cdao.findByNameEditionRarityFoil(dci3.getCard().getName(), dci3.getCard().getEdition(), dci3.getCard().getRarity(), dci3.getCard().isFoil());
                    c3 = findByNameEditionRarityFoil.get(0);
                    dci3.setCard(c3);
                    dci3 = dao.update(dci3);
                    em.getTransaction().commit();
                }
            }
        }

        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardDao dao = new CardDao(em);
            Assert.assertEquals(c2.getName(), dao.findById(c2.getId()).getName());
            Assert.assertEquals(c3.getName(), dao.findById(c3.getId()).getName());
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
            Collection<Card> cards = cardService.saveCardsIntoDb(cardList);
        }
        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardDao dao = new CardDao(em);
            DailyCardInfoDao daoDci = new DailyCardInfoDao(em);
            Assert.assertEquals(CardEdition.ALLIANCES, dao.findByName("Test2", true).get(0).getEdition());
            Assert.assertEquals(1, daoDci.findByCard(dao.findByName("TestInDB", true).get(0)).get(0).getStoreAmount());
        }

    }

}
