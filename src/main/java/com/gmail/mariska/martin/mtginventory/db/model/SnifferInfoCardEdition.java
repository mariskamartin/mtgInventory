package com.gmail.mariska.martin.mtginventory.db.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Slouzi pro zachyceni informaci, ktere edice se maji stahovat
 *
 * @author MAR
 */
public enum SnifferInfoCardEdition {
    intance;

    private Map<CardEdition, CardEditionInfo> managedEditions;

    private SnifferInfoCardEdition() {
        List<CardEditionInfo> managed = Lists.newArrayList();
        //Seznam sledovanych edic
        managed.add(new CardEditionInfo(CardEdition.KHANS_OF_TARKIR, "Khans_of_Tarkir","165"));
        managed.add(new CardEditionInfo(CardEdition.JOURNEY_INTO_NYX, "Journey_Into_Nyx","164"));
        managed.add(new CardEditionInfo(CardEdition.BORN_OF_THE_GODS, "Born_of_Gods","163"));
        managed.add(new CardEditionInfo(CardEdition.THEROS, "Theros","162"));
        managed.add(new CardEditionInfo(CardEdition.MAGIC_2015, "M15", "16"));
        managed.add(new CardEditionInfo(CardEdition.COMMANDER_2014, null, "3037"));

        //cr predprodej
        managed.add(new CardEditionInfo(CardEdition.FATE_REFORGED, "Fate_Reforged", "166"));

        managed.add(new CardEditionInfo(CardEdition.MORNINGTIDE, "Morningtide", "144"));
        managed.add(new CardEditionInfo(CardEdition.LORWYN, "Lorwyn", "143"));
        managed.add(new CardEditionInfo(CardEdition.MAGIC_2014, "M14", null));
        managed.add(new CardEditionInfo(CardEdition.RETURN_TO_RAVNICA, "Return_to_Ravnica", "159"));
        managed.add(new CardEditionInfo(CardEdition.GATECRASH, "Gatecrash","160"));
        managed.add(new CardEditionInfo(CardEdition.DRAGONS_MAZE, "Dragons_Maze", "161"));
        managed.add(new CardEditionInfo(CardEdition.INNISTRAD, "Innistrad", "156"));
        managed.add(new CardEditionInfo(CardEdition.ZENDIKAR, "Zendikar", "150"));
        managed.add(new CardEditionInfo(CardEdition.MODERN_EVENT_DECK, null, "3029"));
        managed.add(new CardEditionInfo(CardEdition.MODERN_MASTERS, "Modern_Masters", "3021"));
        managed.add(new CardEditionInfo(CardEdition.NEW_PHYREXIA, "New_Phyrexia", "155"));

        managedEditions = Maps.newHashMap();
        for (CardEditionInfo cardEditionInfo : managed) {
            managedEditions.put(cardEditionInfo.getEdition(), cardEditionInfo);
        }
    }

    /**
     * Vraci seznam monitorovanych edic
     *
     * @return
     */
    public Collection<CardEdition> getManagedEditions() {
        return Collections.unmodifiableCollection(managedEditions.keySet());
    }

    /**
     * Vrati dodatecn informace k edici
     *
     * @param edition
     * @return
     */
    public CardEditionInfo getInfo(CardEdition edition) {
        return managedEditions.get(edition);
    }

    /**
     * Pro uchovani specifickych informaci k edici
     *
     * @author MAR
     */
    public static class CardEditionInfo {
        private String tolarieUrlKey;
        private CardEdition edition;
        private String rishadaUrlKey;

        public CardEditionInfo(CardEdition edition, String tolarieUrlKey, String rishadaUrlKey) {
            super();
            this.edition = edition;
            this.tolarieUrlKey = tolarieUrlKey;
            this.rishadaUrlKey = rishadaUrlKey;
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

        public String getRishadaUrlKey() {
            return rishadaUrlKey;
        }
    }
}
