package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Date;

/**
 * Slouzi pro generovani karet
 * 
 * @author MAR
 * 
 */
public class CardFactory {

    public static Card createFableHero() {
        Card c2 = new Card();
        c2.setId("ID-FABLEDHERO-1");
        c2.setName("Fabled Hero");
        c2.setEdition(CardEdition.THEROS);
        c2.setRarity(CardRarity.RARE);
        c2.setCreated(new Date());
        return c2;
    }

    public static Card createFableHeroAutoId() {
        Card c2 = new Card();
        c2.setName("Fabled Hero");
        c2.setEdition(CardEdition.THEROS);
        c2.setRarity(CardRarity.RARE);
        c2.setCreated(new Date());
        return c2;
    }

    public static Card create(String name, CardEdition edition, CardRarity rarity) {
        Card c2 = new Card();
        c2.setEdition(edition);
        c2.setName(name);
        c2.setRarity(rarity);
        return c2;
    }

}
