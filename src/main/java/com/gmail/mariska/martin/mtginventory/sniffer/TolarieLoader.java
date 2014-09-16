package com.gmail.mariska.martin.mtginventory.sniffer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardShop;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.service.WebPageSnifferService.CardConverter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TolarieLoader implements ISniffStrategy {

    @Override
    public List<DailyCardInfo> sniffByCardName(String name) throws IOException {
        Builder<DailyCardInfo> builder = ImmutableList.builder();
        parseTolarie(fetchFromTolarieKusovky(name), builder);
        return builder.build();
    }

    @Override
    public List<DailyCardInfo> sniffByEdition(CardEdition edition) throws IOException {
        throw new UnsupportedOperationException("Tato metoda jeste neni zprovoznena");
    }

    /**
     * Parsuje karty na Tolarii
     * 
     * @param cardFindName
     * @param builder
     * @throws IOException
     */
    private void parseTolarie(Document doc, Builder<DailyCardInfo> builder) throws IOException {
        Elements values = doc.select("table.kusovky tbody tr");
        for (Element element : values) {
            Card card = CardConverter.valueOfTolarieElement(element);
            Elements innerValues = element.children();
            long skladem = Long.parseLong(innerValues.get(0)
                    .children()
                    .get(1)
                    .text()
                    .toUpperCase()
                    .replace("KS", "")
                    .replace("SKLADEM", "")
                    .trim());
            long cena = Long.parseLong(innerValues.get(5).text().replace(" Kč", ""));
            DailyCardInfo dci = new DailyCardInfo(card, BigDecimal.valueOf(cena), skladem, new Date(), CardShop.TOLARIE);
            builder.add(dci);
        }
    }


    private Document fetchFromTolarieKusovky(String findString) throws IOException {
        String urlRequest = "http://www.tolarie.cz/koupit_karty/?name=" + findString.replace(" ", "+")
                + "&o=name&od=a";
        Document doc = Jsoup.connect(urlRequest).get();
        return doc;
    }

}
