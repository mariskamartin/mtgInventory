package com.gmail.mariska.martin.mtginventory.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardRarity;
import com.gmail.mariska.martin.mtginventory.db.model.CardShop;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Pro stahovani informaci z webovek
 * 
 * @author MAR
 */
public class WebPageSnifferService {

    /**
     * Pouze pro testovaci ucely
     * @param args
     * @throws IOException
     */
    @Deprecated
    public static void main(String[] args) throws IOException {
        Builder<DailyCardInfo> builder = ImmutableList.builder();
        new WebPageSnifferService().findOnNajada("stifle", builder);
        ImmutableList<DailyCardInfo> build = builder.build();
        System.out.println(build);
    }

    /**
     * Vyhleda na tolarii dany string, kartu
     * 
     * @param cardFindName
     * @return
     * @throws IOException
     */
    public ImmutableList<DailyCardInfo> findCardsAtWeb(String cardFindName) throws IOException {
        Builder<DailyCardInfo> builder = ImmutableList.builder();
        findOnTolarie(cardFindName, builder);
        findOnCernyRytir(cardFindName, builder);
        findOnNajada(cardFindName, builder);
        return builder.build();
    }

    /**
     * Dohleda karty na cernem rytiri
     * 
     * @param cardFindName
     * @param builder
     * @throws IOException
     */
    private void findOnCernyRytir(String cardFindName, Builder<DailyCardInfo> builder) throws IOException {
        Document doc = fetchFromCernyRytirKusovky(cardFindName);
        Elements values = doc.select("table.kusovkytext tbody tr");
        // prvni tri jsou formular, preskocit
        // dale jsou vzdy informace po trech
        if (values.size() >= 6) {
            for (int i = 3; i < values.size(); i = i + 3) {
                Element nameE = values.get(i);
                Element ediceTypE = values.get(i + 1);
                Element dataE = values.get(i + 2);

                Card card = CardConverter.valueOfCernyRytirElement(nameE, ediceTypE, dataE);
                Elements select2 = dataE.select("font");
                long skladem = Long.parseLong(select2.get(0).text().replace(" ks", ""));
                long cena = Long.parseLong(select2.get(1).text().replace(" Kč", ""));

                DailyCardInfo dci = new DailyCardInfo(card, BigDecimal.valueOf(cena), skladem, new Date(),
                        CardShop.CERNY_RYTIR);
                builder.add(dci);
            }
        } else {
            // empty
        }
    }

    /**
     * Dohleda karty na Tolarii
     * 
     * @param cardFindName
     * @param builder
     * @throws IOException
     */
    private void findOnTolarie(String cardFindName, Builder<DailyCardInfo> builder) throws IOException {
        Document doc = fetchFromTolarieKusovky(cardFindName);
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

    /**
     * Dohleda karty na Najada.cz
     * 
     * @param cardFindName
     * @param builder
     * @throws IOException
     */
    private void findOnNajada(String cardFindName, Builder<DailyCardInfo> builder) throws IOException {
        Document doc = fetchFromNajadaKusovky(cardFindName);
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

    private Document fetchFromTolarieKusovky(String findString) throws IOException {
        String urlRequest = "http://www.tolarie.cz/koupit_karty/?name=" + findString.replace(" ", "+")
                + "&o=name&od=a";
        Document doc = Jsoup.connect(urlRequest).get();
        return doc;
    }

    private Document fetchFromCernyRytirKusovky(String cardName) throws IOException {
        Document doc;
        doc = Jsoup.connect("http://www.cernyrytir.cz/index.php3?akce=3")
                .data("edice_magic", "libovolna")
                .data("rarita", "A")
                .data("foil", "A")
                .data("jmenokarty", cardName)
                .data("triditpodle", "ceny")
                .data("submit", "Vyhledej")
                // and other hidden fields which are being passed in post request.
                .userAgent("Mozilla")
                .post();
        return doc;
// return Jsoup.parse(new File("C://cr.html"), "utf-8"); //for DEBUG
    }

    private Document fetchFromNajadaKusovky(String findString) throws IOException {
        String urlRequest = "http://www.najada.cz/cz/kusovky-mtg/?Search=" + findString.replace(" ", "+")
                + "&Sender=Submit&MagicCardSet=-1";
        Document doc = Jsoup.connect(urlRequest).get();
        return doc;
//        return Jsoup.parse(new File("C://najada.html"), "utf-8"); //for DEBUG
    }


    /**
     * Trida pro transormaci elementu na entity
     * 
     * @author MAR
     */
    public static final class CardConverter {
        private static final String FOIL_CR = " - foil";
        private static final String FOIL_TOLARIE = "(foil)";

        private CardConverter() {
        }

        public static Card valueOfTolarieElement(Element element) {
            Card c = new Card();
            Elements innerValues = element.children();
            String name = innerValues.get(0).children().get(0).text().trim();
            c.setFoil(name.contains(FOIL_TOLARIE));
            c.setName(name.replaceAll(Pattern.quote(FOIL_TOLARIE), "").trim());
            c.setRarity(CardRarity.valueFrom(innerValues.get(3).text().trim().toUpperCase()));
            c.setEdition(CardEdition.valueFromName(innerValues.get(4).text().trim()));
            return c;
        }

        public static Card valueOfCernyRytirElement(Element nameE, Element ediceTypE, Element dataE) {
            Card c = new Card();
            String name = nameE.select("div").first().text();
            c.setFoil(name.contains(FOIL_CR));
            c.setName(name.replaceAll("´", "'").replaceAll(Pattern.quote(FOIL_CR), "").trim());
            c.setRarity(CardRarity.valueFrom(dataE.select("td").first().text().toUpperCase()));
            c.setEdition(CardEdition.valueFromName(ediceTypE.select("td").get(0).text()));
            return c;
        }

        public static Card valueOfNajadaElement(Element element) {
            Card c = new Card();
            Elements innerValues = element.children();
            c.setName(innerValues.get(0).text().trim());
            c.setFoil(innerValues.get(1).text().toLowerCase().contains("ano"));
            c.setRarity(CardRarity.valueFrom(innerValues.get(5).text().trim().toUpperCase()));
            c.setEdition(CardEdition.valueFromName(innerValues.get(6).text().trim()));
            return c;
        }
    }
}
