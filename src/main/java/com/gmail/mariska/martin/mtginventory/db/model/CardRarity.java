package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public enum CardRarity {
    RARE, MYTHIC("Mythic Rare"), UNCOMMON, COMMON, UNKNOWN("PROMO"), TOKEN, LAND("basic land", "land");

    private static final Logger logger = Logger.getLogger(CardRarity.class);
    private List<String> alternativeNames;

    private CardRarity(String... alternatives) {
        if (alternatives.length > 0) {
            this.alternativeNames = new LinkedList<String>();
            for (String string : alternatives) {
                alternativeNames.add(string.toUpperCase());
            }
        }
    }

    public static CardRarity valueFrom(String rarityName) {
        try {
            return CardRarity.valueOf(rarityName);
        } catch (IllegalArgumentException e) {
            String name = rarityName.toUpperCase();
            for (CardRarity rarity : values()) {
                if (rarity.alternativeNames != null) {
                    if (rarity.alternativeNames.contains(name)) {
                        return rarity;
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("no recognized rarity: " + rarityName);
            }
            return CardRarity.UNKNOWN;
        }
    }
}
