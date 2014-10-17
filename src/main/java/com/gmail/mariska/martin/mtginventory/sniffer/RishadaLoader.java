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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class RishadaLoader implements ISniffer {

    @Override
    public List<DailyCardInfo> sniffByCardName(String name) throws IOException {
        Builder<DailyCardInfo> builder = ImmutableList.builder();
        parse(fetchKusovky(name), builder);
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
    private void parse(Document doc, Builder<DailyCardInfo> builder) throws IOException {
        Elements values = doc.select("div.results table tbody tr");
        if (!values.isEmpty()) {
            values.remove(0); // hlavicka
        }
        for (Element element : values) {
            Card card = CardConverter.valueOfRishadaElement(element);
            Elements innerValues = element.children();
            long cena = Long.parseLong((innerValues.get(5).text().replace("Kč", "").replace(" ", ""))); //tady je uveden specialni znak ktery pouzivaji  &nbsp;
            long skladem = Long.parseLong(innerValues.get(6).text().trim());
            DailyCardInfo dci = new DailyCardInfo(card, BigDecimal.valueOf(cena), skladem, new Date(), CardShop.RISHADA);
            builder.add(dci);
        }
    }

    private Document fetchKusovky(String findString) throws IOException {
        String urlRequest = "http://www.rishada.cz/kusovky/vysledky-hledani?searchtype=basic&xxwhichpage=1&xxcardname="
                + findString.replace(" ", "+")
                + "&xxedition=1000000&xxpagesize=1000&search=Vyhledat";
        Document doc = Jsoup.connect(urlRequest).get();
        return doc;
//        return Jsoup.parse(new File("C://rishada.html"), "utf-8"); //for DEBUG
    }


    public static void main(String[] args) throws IOException {
        RishadaLoader r = new RishadaLoader();
//        Document doc = r.fetchKusovky("hammer of");
        System.out.println(r.sniffByCardName("hammer of"));
    }
}
