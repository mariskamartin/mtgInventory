package com.gmail.mariska.martin.mtginventory.db;

import com.gmail.mariska.martin.mtginventory.db.model.*;
import com.gmail.mariska.martin.mtginventory.service.CardService;
import com.gmail.mariska.martin.objectdb.OdbTestSuite;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DailyCardInfoDaoTest extends OdbTestSuite {

    @Test
    public void testGetAllWithLimit() throws Exception {
        try (ClosableEM em = OdbTestSuite.getClosableEM()) {
            CardService cardService = new CardService(em, null, null, new CardDao(em), new BannedCardNamesDao(em));

            Card c3 = CardFactory.create("Test2", CardEdition.ALLIANCES, CardRarity.COMMON);
            DailyCardInfo dci3 = new DailyCardInfo(c3, new BigDecimal(150), 3, new Date(), CardShop.NAJADA);
            Card c4 = CardFactory.create("Test2", CardEdition.ALLIANCES, CardRarity.COMMON);
            DailyCardInfo dci4 = new DailyCardInfo(c4, new BigDecimal(10), 5, new Date(), CardShop.TOLARIE);
            Card c2 = CardFactory.create("TestInDB", CardEdition.ANTIQUITIES, CardRarity.COMMON);
            DailyCardInfo dci5 = new DailyCardInfo(c2, new BigDecimal(5), 1, new Date(), CardShop.TOLARIE);

            Collection<Card> cards = cardService.saveCardsIntoDb(Arrays.asList(dci3, dci4, dci5), true);

            DailyCardInfoDao dao = new DailyCardInfoDao(em);
            List<DailyCardInfo> all = dao.limit(1).getAll();
            assertEquals(1, all.size());
            all = dao.limit(2).getAll();
            assertEquals(2, all.size());
            all = dao.limit(2).startAt(3).getAll();
            assertEquals(0, all.size());
        }
    }
}