package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

public class CardRarityTest {

    @Test
    public void test() {
        Map<String, CardRarity> names = Maps.newHashMap();
        names.put("nevim", CardRarity.UNKNOWN);
        names.put("rare", CardRarity.RARE);
        names.put("land", CardRarity.LAND);
        names.put("basic land", CardRarity.LAND);
        names.put("mythic", CardRarity.MYTHIC);
        names.put("mythic rare", CardRarity.MYTHIC);
        names.put("token", CardRarity.TOKEN);
        names.put("uncommon", CardRarity.UNCOMMON);
        names.put("common", CardRarity.COMMON);

        for (int i = 0; i < 10; i++) {
            for (String name : names.keySet()) {
                CardRarity valueFromName = CardRarity.valueFrom(name);
                Assert.assertEquals(names.get(name), valueFromName);
            }
        }
    }

}
