package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

public enum CardEdition {
    ALARA_REBORN("ARB", "Alara Reborn"),
    ALLIANCES("ALL", "Alliances"),
    ALPHA("LEA", "Alpha"),
    ANTIQUITIES("ATQ", "Antiquities"),
    APOCALYPSE("APC", "Apocalypse"),
    ARABIAN_NIGHTS("ARN", "Arabian Nights"),
    ARCHENEMY("ARC", "Archenemy"),
    AVACYN_RESTORED("AVR", "Avacyn Restored"),
    BETA("LEB", "Beta", "Limited Edition Beta", "Edition Beta"),
    BEATDOWN_BOX_SET("BDbs", "Beatdown Box Set"),
    BATTLE_ROYALE_BOX_SET("BRbs", "Battle Royale Box Set"),
    BETRAYERS_OF_KAMIGAWA("BOK", "Betrayers of Kamigawa"),
    BORN_OF_THE_GODS("BNG", "Born of the Gods", "Born of Gods"),
    CHAMPIONS_OF_KAMIGAWA("CHK", "Champions of Kamigawa"),
    CHRONICLES("CHR", "Chronicles"),
    COLDSNAP("CSP", "Coldsnap"),
    COMMANDER("CMD", "Commander", "Magic: The Gathering-Commander"),
    COMMANDER_2013("C13", "Commander 2013", "Commander 2013 Edition"),
    COMMANDERS_ARSENAL("CMA", "Commander's Arsenal"),
    CONFLUX("CON", "Conflux"),
    CONSPIRACY("CNS", "Conspiracy","Magic: The Gathering—Conspiracy"),
    CLASH_PACK_2015("CP15", "Clash Pack 2015","Clash Pack 2015 foils"),
    DARK_ASCENSION("DKA", "Dark Ascension"),
    DARKSTEEL("DST", "Darksteel"),
    DD_AJANI_VS_NICOL_BOLAS("DDAvB", "DD: Ajani vs. Nicol Bolas", "Duel Decks: Ajani vs. Nicol Bolas", "Ajani vs. Nicol Bolas"),
    DD_DIVINE_VS_DEMONIC("DDDvD", "DD: Divine vs. Demonic", "Duel Decks: Divine vs. Demonic", "Divine vs. Demonic"),
    DD_ELSPETH_VS_TEZZERET("DDEvT", "DD: Elspeth vs. Tezzeret", "Duel Decks: Elspeth vs. Tezzeret", "Elspeth vs. Tezzeret"),
    DD_ELVES_VS_GOBLINS("DDEvG", "DD: Elves vs. Goblins", "Duel Decks: Elves vs. Goblins", "Elves vs. Goblins"),
    DD_GARRUK_VS_LILIANA("DDGvL", "DD: Garruk vs. Liliana", "Duel Decks: Garruk vs. Liliana", "Garruk vs. Liliana"),
    DD_HEROES_VS_MONSTERS("DDHvM", "DD: Heroes vs. Monsters", "Duel Decks: Heroes vs. Monsters", "Heroes vs. Monsters"),
    DD_IZZET_VS_GOLGARI("DDIvG", "DD: Izzet vs. Golgari", "Duel Decks: Izzet vs. Golgari","Izzet vs. Golgari"),
    DD_JACE_VS_CHANDRA("DDJvC", "DD: Jace vs. Chandra", "Duel Decks: Jace vs. Chandra", "Jace vs. Chandra"),
    DD_JACE_VS_VRASKA("DDJvV", "DD: Jace vs. Vraska", "Duel Decks: Jace vs. Vraska","Jace vs. Vraska"),
    DD_KNIGHTS_VS_DRAGONS("DDKvD", "DD: Knights vs Dragons", "DD: Knights vs. Dragons", "Duel Decks: Knights vs. Dragons", "Knights vs. Dragons"),
    DD_PHYREXIA_VS_COALITION("DDPvC", "DD: Phyrexia vs. Coalition", "Duel Decks: Phyrexia vs. Coalition", "Phyrexia vs. Coalition", "Duel Decks: Phyrexia vs. the Coalition", "Phyrexia vs. the Coalition"),
    DD_SPEED_VS_CUNNING("DDSvC", "DD: Speed vs. Cunning", "Duel Decks: Speed vs. Cunning", "Speed vs. Cunning"),
    DD_SORIN_VS_TIBALT("DDSvT", "DD: Sorin vs. Tibalt", "Duel Decks: Sorin vs. Tibalt", "Sorin vs. Tibalt"),
    DD_VENSER_VS_KOTH("DDVvK", "DD: Venser vs. Koth", "Duel Decks: Venser vs. Koth", "Venser vs. Koth"),
    DISSENSION("DIS", "Dissension"),
    DRAGONS_MAZE("DGM", "Dragon's Maze"),
    EDITION_3TH("3ED", "3rd Edition", "Thirdth Edition", "Revised Edition"),
    EDITION_4TH("4ED", "4th Edition", "Fourth Edition"),
    EDITION_5TH("5ED", "5th Edition", "Fifth Edition"),
    EDITION_6TH("6ED", "6th Edition", "Classic Sixth Edition", "Sixth Edition"),
    EDITION_7TH("7ED", "7th Edition", "Seventh Edition"),
    EDITION_8TH("8ED", "8th Edition", "Eighth Edition"),
    EDITION_9TH("9ED", "9th Edition"),
    EDITION_10TH("10E", "10th Edition"),
    EVENTIDE("EVE", "Eventide"),
    EXODUS("EXO", "Exodus"),
    FALLEN_EMPIRES("FEM", "Fallen Empires"),
    FIFTH_DAWN("5DN", "Fifth Dawn"),
    FTV_ANNIHILATION("FTVA", "FTV: Annihilation", "From the Vault: Annihilation"),
    FTV_DRAGONS("FTVD", "FTV: Dragons", "From the Vault: Dragons"),
    FTV_EXILED("FTVE", "FTV: Exiled", "From the Vault: Exiled"),
    FTV_LEGENDS("FTVL", "FTV: Legends", "From the Vault: Legends"),
    FTV_REALMS("FTVRlm", "FTV: Realms", "From the Vault: Realms"),
    FTV_RELICS("FTVRlc", "FTV: Relics", "From the Vault: Relics"),
    FTV_TWENTY("FTV20", "FTV: Twenty", "From the Vault: Twenty"),
    FUTURE_SIGHT("FUT", "Future Sight"),
    GATECRASH("GTC", "Gatecrash"),
    GUILDPACT("GPT", "Guildpact"),
    HOMELANDS("HML", "Homelands"),
    ICE_AGE("ICE", "Ice Age"),
    INNISTRAD("ISD", "Innistrad"),
    INVASION("INV", "Invasion"),
    JOURNEY_INTO_NYX("JOU", "Journey into Nyx"),
    JUDGMENT("JUD", "Judgment"),
    KHANS_OF_TARKIR("KTK", "Khans of Tarkir"),
    LEGENDS("LEG", "Legends"),
    LEGIONS("LGN", "Legions"),
    LORWYN("LRW", "Lorwyn"),
    MAGIC_2010("M10", "Magic 2010", "MAGIC 10"),
    MAGIC_2011("M11", "Magic 2011", "MAGIC 11"),
    MAGIC_2012("M12", "Magic 2012", "MAGIC 12"),
    MAGIC_2013("M13", "Magic 2013", "MAGIC 13"),
    MAGIC_2014("M14", "Magic 2014", "MAGIC 14", "Magic 2014 Core Set"),
    MAGIC_2015("M15", "Magic 2015", "MAGIC 15", "Magic 2015 Core Set"),
    MERCADIAN_MASQUES("MMQ", "Mercadian Masques"),
    MIRAGE("MIR", "Mirage"),
    MIRRODIN("MRD", "Mirrodin"),
    MIRRODIN_BESIEGED("MBS", "Mirrodin Besieged"),
    MODERN_EVENT_DECK("MED", "Modern Event Deck", "Modern Event Deck 2014"),
    MODERN_MASTERS("MMA", "Modern Masters"),
    MORNINGTIDE("MOR", "Morningtide"),
    NEMESIS("NMS", "Nemesis"),
    NEW_PHYREXIA("NPH", "New Phyrexia"),
    ODYSSEY("ODY", "Odyssey"),
    ONSLAUGHT("ONS", "Onslaught"),
    P_ARENA("Parena", "P - Arena"),
    P_BUY_A_BOX("Pbox", "P - Buy a Box"),
    P_FRIDAY_NIGHT_MAGIC("Pfnm", "P - Friday Night Magic", "FNM Promo"),
    P_GAMEDAY_CHAMPS("Pgd", "P - Gameday, Champs"),
    P_GATEWAY("Pgw", "P - Gateway"),
    P_GP_PT_JSS("Pgp", "P - GP, PT, JSS"),
    P_JUDGE_REWARDS("Pjudge", "P - Judge Rewards", "Judge rewards"),
    P_MEDIA_INSERTS("Pmedia", "P - Media Inserts"),
    P_MISCELLANEOUS("P", "P - Miscellaneous", "Promo karty", "Promo"),
    P_PLAYER_REWARDS("Preward", "P - Player Rewards"),
    P_PRERELEASE_RELEASE("Prel", "P - Prerelease, Release"),
    PD_FIRE_AND_LIGHTNING("PDL", "PD: Fire and Lightning", "Premium Deck Series: Fire and Lightning", "PDS: Fire and Lightning"),
    PD_GRAVEBORN("PDG", "PD: Graveborn","Premium Deck Series: Graveborn", "PDS: Graveborn"),
    PD_SLIVERS("PDS", "PD: Slivers", "Premium Deck Series: Slivers", "PDS: Slivers"),
    PLANAR_CHAOS("PLC", "Planar Chaos"),
    PLANECHASE("HOP", "Planechase"),
    PLANECHASE_2012("PC2", "Planechase 2012", "Planechase 2012 Edition"),
    PLANESHIFT("PLS", "Planeshift"),
    PORTAL("POR", "Portal"),
    PORTAL_SECOND_AGE("P02", "Portal Second Age"),
    PORTAL_THREE_KINGDOMS("PTK", "Portal Three Kingdoms"),
    PROPHECY("PCY", "Prophecy"),
    RAVNICA("RAV", "Ravnica", "Ravnica: City of Guilds"),
    RETURN_TO_RAVNICA("RTR", "Return to Ravnica"),
    RISE_OF_THE_ELDRAZI("ROE", "Rise of the Eldrazi"),
    SAVIORS_OF_KAMIGAWA("SOK", "Saviors of Kamigawa"),
    SCARS_OF_MIRRODIN("SOM", "Scars of Mirrodin"),
    SCOURGE("SCG", "Scourge"),
    SHADOWMOOR("SHM", "Shadowmoor"),
    SHARDS_OF_ALARA("ALA", "Shards of Alara"),
    STARTER_1999("S99", "Starter 1999"),
    STRONGHOLD("STH", "Stronghold"),
    TEMPEST("TMP", "Tempest"),
    THE_DARK("DRK", "The Dark"),
    THEROS("THS", "Theros"),
    TIME_SPIRAL("TSP", "Time Spiral"),
    TIMESHIFTED("TSB", "Timeshifted", "Time Spiral \"Timeshifted\""),
    TORMENT("TOR", "Torment"),
    UNGLUED("UGL", "Unglued"),
    UNHINGED("UNH", "Unhinged", "Unhinged Edition"),
    UNLIMITED("2ED", "Unlimited", "Unlimited Edition"),
    URZAS_DESTINY("UDS", "Urza's Destiny"),
    URZAS_LEGACY("ULG", "Urza's Legacy"),
    URZAS_SAGA("USG", "Urza's Saga"),
    VISIONS("VIS", "Visions"),
    WEATHERLIGHT("WTH", "Weatherlight"),
    WORLDWAKE("WWK", "Worldwake"),
    ZENDIKAR("ZEN", "Zendikar"),
    WHITE_BORDERED("WBBS", "Whitebordered Basic sets"),
    BLACK_BORDERED("BBBS", "Blackbordered Basic Sets"),
    UNKNOWN("???", "Unknown", "Legacy", "Foreign Black Bordered");


    private static final Logger logger = Logger.getLogger(CardEdition.class);
    private static final Map<String, CardEdition> cache = Maps.newHashMap();
    private String name;
    private List<String> alternativeName;
    private String key;

    private CardEdition(String key, String name, String... alternatives) {
        this.name = name;
        this.key = key;
        if (alternatives.length > 0) {
            this.alternativeName = new LinkedList<String>();
            for (String string : alternatives) {
                alternativeName.add(string.toUpperCase());
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public static CardEdition valueFromName(String editionName) {
        String name = editionName.replace("´", "'").toUpperCase();
        CardEdition cardEdition = cache.get(name);
        if (cardEdition == null) {
            for (CardEdition e : values()) {
                if (e.name.toUpperCase().equals(name)) {
                    cache.put(name, e);
                    return e;
                } else if (e.alternativeName != null) {
                    if (e.alternativeName.contains(name)) {
                        cache.put(name, e);
                        return e;
                    }
                }
            }
        } else {
            return cardEdition;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("no recognized edition: " + editionName);
        }
        return UNKNOWN;
    }
}
