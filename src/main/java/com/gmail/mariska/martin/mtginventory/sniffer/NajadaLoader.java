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

public class NajadaLoader implements ISniffStrategy {

    @Override
    public List<DailyCardInfo> sniffByCardName(String name) throws IOException {
        Builder<DailyCardInfo> builder = ImmutableList.builder();
        parseNajada(fetchFromNajadaKusovky(name), builder);
        return builder.build();
    }

    @Override
    public List<DailyCardInfo> sniffByEdition(CardEdition edition) throws IOException {
        throw new UnsupportedOperationException("Tato metoda jeste neni zprovoznena");
    }

    /**
     * Parsuje karty na Najada.cz
     * 
     * @param cardFindName
     * @param builder
     * @throws IOException
     */
    private void parseNajada(Document doc, Builder<DailyCardInfo> builder) throws IOException {
        Elements values = doc.select("table.tabArt tbody tr");
        if (!values.isEmpty()) {
            values.remove(0); //hlavicka
        }
        for (Element element : values) {
            Card card = CardConverter.valueOfNajadaElement(element);
            Elements innerValues = element.children();
            long skladem = Long.parseLong(innerValues.get(10).select("span").get(2).text().trim());
            long cena = Long.parseLong(innerValues.get(10).select("span.v").text().replace(" ", "").trim());
            DailyCardInfo dci = new DailyCardInfo(card, BigDecimal.valueOf(cena), skladem, new Date(), CardShop.NAJADA);
            builder.add(dci);
        }
    }

    private Document fetchFromNajadaKusovky(String findString) throws IOException {
        String urlRequest = "http://www.najada.cz/cz/kusovky-mtg/?Search=" + findString.replace(" ", "+")
                + "&Sender=Submit&MagicCardSet=-1";
        Document doc = Jsoup.connect(urlRequest).get();
        return doc;
//        return Jsoup.parse(new File("C://najada.html"), "utf-8"); //for DEBUG
    }

}
