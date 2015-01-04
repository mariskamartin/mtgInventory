package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Slouzi pro zachyceni informaci, ktere edice se maji stahovat
 * 
 * @author MAR
 * 
 */
public enum SnifferInfoCardEdition {
    intance;

    private Map<CardEdition, CardEditionInfo> managedEditions;

    private SnifferInfoCardEdition() {
        List<CardEditionInfo> managed = Lists.newArrayList();
        //Seznam sledovanych edic
//        managed.add(new CardEditionInfo(CardEdition.KHANS_OF_TARKIR, "Khans_of_Tarkir"));
        managed.add(new CardEditionInfo(CardEdition.JOURNEY_INTO_NYX, "Journey_Into_Nyx"));
//        managed.add(new CardEditionInfo(CardEdition.BORN_OF_THE_GODS, "Born_of_Gods"));
//        managed.add(new CardEditionInfo(CardEdition.THEROS, "Theros"));
//        managed.add(new CardEditionInfo(CardEdition.MAGIC_2015, "M15"));
//        managed.add(new CardEditionInfo(CardEdition.COMMANDER_2014, null));

//        managed.add(new CardEditionInfo(CardEdition.MAGIC_2014, "M14"));
//        managed.add(new CardEditionInfo(CardEdition.RETURN_TO_RAVNICA, "Return_to_Ravnica"));
//        managed.add(new CardEditionInfo(CardEdition.GATECRASH, "Gatecrash"));
//        managed.add(new CardEditionInfo(CardEdition.DRAGONS_MAZE, "Dragons_Maze"));
//        managed.add(new CardEditionInfo(CardEdition.INNISTRAD, "Innistrad"));
//        managed.add(new CardEditionInfo(CardEdition.ZENDIKAR, "Zendikar"));

        managedEditions = Maps.newHashMap();
        for (CardEditionInfo cardEditionInfo : managed) {
            managedEditions.put(cardEditionInfo.getEdition(), cardEditionInfo);
        }
    }

    /**
     * Vraci seznam monitorovanych edic
     * @return
     */
    public Collection<CardEdition> getManagedEditions() {
        return Collections.unmodifiableCollection(managedEditions.keySet());
    }

    /**
     * Vrati dodatecn informace k edici
     * @param edition
     * @return
     */
    public CardEditionInfo getInfo(CardEdition edition) {
        return managedEditions.get(edition);
    }

    /**
     * Pro uchovani specifickych informaci k edici
     * @author MAR
     *
     */
    public static class CardEditionInfo {
        private String tolarieUrlKey;
        private CardEdition edition;

        public CardEditionInfo(CardEdition edition, String tolarieUrlKey) {
            super();
            this.edition = edition;
            this.tolarieUrlKey = tolarieUrlKey;
        }

        public String getTolarieUrlKey() {
            return tolarieUrlKey;
        }

        public String getCernyRytirUrlKey() {
            return edition.getKey();
        }

        public CardEdition getEdition() {
            return edition;
        }
    }
}
