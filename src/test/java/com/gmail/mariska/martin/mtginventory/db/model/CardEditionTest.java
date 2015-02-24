package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

public class CardEditionTest {

    @Test
    public void test() {
        Map<String, CardEdition> names = Maps.newHashMap();
        names.put("Legacy", CardEdition.UNKNOWN);
        names.put("Time Spiral",CardEdition.TIME_SPIRAL);
        names.put("Shadowmoor",CardEdition.SHADOWMOOR);
        names.put("Premium Deck Series: Fire and Lightning",CardEdition.PD_FIRE_AND_LIGHTNING);
        names.put("P - Media Inserts", CardEdition.P_MEDIA_INSERTS);
        names.put("MAGIC 14", CardEdition.MAGIC_2014);
        names.put("PD: Fire and Lightning", CardEdition.PD_FIRE_AND_LIGHTNING);
        names.put("Magic 2014 Core Set", CardEdition.MAGIC_2014);
        names.put("Magic 2014", CardEdition.MAGIC_2014);
        names.put("Legions", CardEdition.LEGIONS);
        names.put("From the Vault: Legends", CardEdition.FTV_LEGENDS);
        for (int i = 0; i < 10; i++) {
            for (String name : names.keySet()) {
                CardEdition valueFromName = CardEdition.valueFromName(name);
                Assert.assertEquals(names.get(name), valueFromName);
            }
        }
    }

}
