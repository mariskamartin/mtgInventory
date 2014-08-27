package com.gmail.mariska.martin.mtginventory.db.model;

public enum CardRarity {
    RARE, MYTHIC, UNCOMMON, COMMON, UNKNOWN;

    public static CardRarity valueFrom(String upperCase) {
        try {
            return CardRarity.valueOf(upperCase);
        } catch (IllegalArgumentException e) {
            return CardRarity.UNKNOWN;
        }
    }
}
