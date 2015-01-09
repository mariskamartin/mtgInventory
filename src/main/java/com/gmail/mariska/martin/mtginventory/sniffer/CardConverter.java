package com.gmail.mariska.martin.mtginventory.sniffer;

import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardRarity;

/**
 * Trida pro transormaci elementu na entity
 * 
 * @author MAR
 */
public final class CardConverter {
    private static final String CR_FOIL = " - foil";
    private static final String FOIL_TOLARIE = "(foil)";

    private static final String CR_PREDOBJEDNAVKA = " (předobjednávka, vychází 23.1)";

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
        c.setFoil(name.contains(CR_FOIL));
        c.setName(name.replaceAll("´", "'")
                .replaceAll(Pattern.quote(CR_FOIL), "")
                .replaceAll(Pattern.quote(CR_PREDOBJEDNAVKA), "")
                .trim());
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

    public static Card valueOfRishadaElement(Element element) {
        Card c = new Card();
        Elements innerValues = element.children();
        c.setName(innerValues.get(0).text().trim());
        c.setFoil(innerValues.get(1).text().toLowerCase().contains("ano"));
        c.setRarity(CardRarity.valueFrom(innerValues.get(4).text().trim().toUpperCase()));
        c.setEdition(CardEdition.valueFromName(innerValues.get(1).text().trim()));
        return c;
    }

}
