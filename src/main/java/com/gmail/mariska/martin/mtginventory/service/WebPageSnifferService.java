package com.gmail.mariska.martin.mtginventory.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardRarity;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.sniffer.CernyRytirLoader;
import com.gmail.mariska.martin.mtginventory.sniffer.ISniffStrategy;
import com.gmail.mariska.martin.mtginventory.sniffer.NajadaLoader;
import com.gmail.mariska.martin.mtginventory.sniffer.RishadaLoader;
import com.gmail.mariska.martin.mtginventory.sniffer.TolarieLoader;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Pro stahovani informaci z webovek
 * 
 * @author MAR
 */
public class WebPageSnifferService {
    private static final Logger logger = Logger.getLogger(WebPageSnifferService.class);
    private static final int NUM_THREADS = 10;

    /**
     * Pouze pro testovaci ucely
     * 
     * @param args
     * @throws IOException
     */
    @Deprecated
    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        WebPageSnifferService sniffer = new WebPageSnifferService(executor);
        Stopwatch stopky = Stopwatch.createStarted();
        List<DailyCardInfo> list = new ArrayList<>();
//        list.addAll(sniffer.findCardsAtWeb(CardEdition.JOURNEY_INTO_NYX));
//        list.addAll(sniffer.findCardsAtWeb(CardEdition.THEROS));
        list.addAll(sniffer.findCardsAtWeb("stifle", "soldier"));

        System.out.println(stopky.stop().elapsed(TimeUnit.MILLISECONDS));
        System.out.println(list);
        executor.shutdown();

// Builder<DailyCardInfo> builder = ImmutableList.builder();
// // sniffer.parseNajada(sniffer.fetchFromNajadaKusovky("stifle"), builder);
// Document doc = sniffer.fetchFromCernyRytirKusovkyPaged(CardEdition.MAGIC_2015, "M");
// Elements select = doc.select("a.kusovkytext");
// if (select.size() == 0) {
// sniffer.parseCernyRytir(doc, builder);
// } else {
// //pouze polovinu odkazu, protoze CR je ma 2x na strance
// for (int i = 0; i < select.size() / 2; i++) {
// String href = select.get(i).attributes().get("href");
// Document docPaged = sniffer.fetchFromCernyRytirURL(href);
// sniffer.parseCernyRytir(docPaged, builder);
// }
// }
// ImmutableList<DailyCardInfo> build = builder.build();
// System.out.println(build);
    }

    private ExecutorService executor;
    private List<ISniffStrategy> loaders;

    protected WebPageSnifferService(ExecutorService executor, List<ISniffStrategy> loaders) {
        this.executor = executor;
        this.loaders = loaders;
    }

    protected WebPageSnifferService(ExecutorService executor) {
        this(executor, Arrays.asList(new TolarieLoader(), new CernyRytirLoader(), new NajadaLoader(), new RishadaLoader()));
    }

    /**
     * Vyhleda na tolarii dany string, kartu
     * 
     * @param cardFindName
     * @return
     * @throws IOException
     */
    public ImmutableList<DailyCardInfo> findCardsAtWeb(final String... cardFindNames) throws IOException {

        Builder<DailyCardInfo> builder = ImmutableList.builder();
        List<Future<List<DailyCardInfo>>> futures = new ArrayList<>();

        for (final String cardFindName : cardFindNames) {
            for (final ISniffStrategy loader : loaders) {
                futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
                    @Override
                    public List<DailyCardInfo> call() throws Exception {
                        return loader.sniffByCardName(cardFindName);
                    }
                }));
            }
//            futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
//                @Override
//                public List<DailyCardInfo> call() throws Exception {
//                    return new TolarieLoader().sniffByCardName(cardFindName);
//                }
//            }));
//            futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
//                @Override
//                public List<DailyCardInfo> call() throws Exception {
//                    return new CernyRytirLoader().sniffByCardName(cardFindName);
//                }
//            }));
//            futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
//                @Override
//                public List<DailyCardInfo> call() throws Exception {
//                    return new NajadaLoader().sniffByCardName(cardFindName);
//                }
//            }));
        }

        for (Future<List<DailyCardInfo>> future : futures) {
            try {
                builder.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage());
            }
        }

        return builder.build();
    }

    /**
     * Dohledat karty a nacist dayli info podle edic, tim by melo byt hromadne nacitani mnohem rychlejsi
     * 
     * @param editions
     * @return
     */
    public ImmutableList<DailyCardInfo> findCardsAtWeb(CardEdition... editions) {
        Builder<DailyCardInfo> builder = ImmutableList.builder();
        List<Future<List<DailyCardInfo>>> futures = new ArrayList<>();

        for (CardEdition cardEdition : editions) {
            final CardEdition cardEditionFind = cardEdition;
            System.out.println(cardEditionFind);
            futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
                @Override
                public List<DailyCardInfo> call() throws Exception {
                    return new CernyRytirLoader().sniffByEdition(cardEditionFind);
                }
            }));
            // TODO doplnit ostatni obchody
        }

        for (Future<List<DailyCardInfo>> future : futures) {
            try {
                builder.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage());
            }
        }

        return builder.build();
    }

    /**
     * Trida pro transormaci elementu na entity
     * 
     * @author MAR
     */
    public static final class CardConverter {
        private static final String CR_FOIL = " - foil";
        private static final String FOIL_TOLARIE = "(foil)";

        private static final String CR_PREDOBJEDNAVKA = " (předobjednávka, vychází 26.9)";

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
        /*
<tr bgcolor="#303037">
 <td class="tbody"><a title="header=[Hammer of Bogardan] body=[&lt;img src=&quot;/small/Judge Rewards/Hammer of Bogardan (1).jpg&quot; style=&quot;boxovercard&quot; /&gt;&lt;div class=&quot;boxovertext&quot;&gt;Judge rewards (Special)&lt;br /&gt;&lt;img src=&quot;/pic/mana/small/1big.gif&quot; width=&quot;12&quot; height=&quot;12&quot; alt=&quot;((1) bezbarv&aacute;)&quot; title=&quot;((1) bezbarv&aacute;)&quot; /&gt;&lt;img src=&quot;/pic/mana/small/rbig.gif&quot; width=&quot;12&quot; height=&quot;12&quot; alt=&quot;(červen&aacute;)&quot; title=&quot;(červen&aacute;)&quot; /&gt;&lt;img src=&quot;/pic/mana/small/rbig.gif&quot; width=&quot;12&quot; height=&quot;12&quot; alt=&quot;(červen&aacute;)&quot; title=&quot;(červen&aacute;)&quot; /&gt;&lt;br /&gt;Sorcery&lt;br /&gt;Hammer of Bogardan deals 3 damage to target creature or player.&lt;br /&gt;&lt;img src=&quot;/pic/mana/small/2big.gif&quot; width=&quot;12&quot; height=&quot;12&quot; alt=&quot;(&lt;i&gt;(2)&lt;/i&gt; bezbarv&aacute;)&quot; title=&quot;(&lt;i&gt;(2)&lt;/i&gt; bezbarv&aacute;)&quot; /&gt;&lt;img src=&quot;/pic/mana/small/rbig.gif&quot; width=&quot;12&quot; height=&quot;12&quot; alt=&quot;(červen&aacute;)&quot; title=&quot;(červen&aacute;)&quot; /&gt;&lt;img src=&quot;/pic/mana/small/rbig.gif&quot; width=&quot;12&quot; height=&quot;12&quot; alt=&quot;(červen&aacute;)&quot; title=&quot;(červen&aacute;)&quot; /&gt;&lt;img src=&quot;/pic/mana/small/rbig.gif&quot; width=&quot;12&quot; height=&quot;12&quot; alt=&quot;(červen&aacute;)&quot; title=&quot;(červen&aacute;)&quot; /&gt;: Return Hammer of Bogardan from your graveyard to your hand. Play this ability only during your upkeep.
&lt;br /&gt;&lt;/div&gt;]" rel="lightbox" href="/big/Judge Rewards/Hammer of Bogardan (judge foil).jpg">Hammer of Bogardan</a> (judge foil)</td>
 <td class="tbody">Judge rewards</td>
 <td class="tbody"><img src="/pic/mana/small/1big.gif" width="12" height="12" alt="[(1) bezbarv&aacute;]" title="[(1) bezbarv&aacute;]" /><img src="/pic/mana/small/rbig.gif" width="12" height="12" alt="[červen&aacute;]" title="[červen&aacute;]" /><img src="/pic/mana/small/rbig.gif" width="12" height="12" alt="[červen&aacute;]" title="[červen&aacute;]" /></td>
 <td class="tbody">Near Mint</td>
 <td class="tbody">Special</td>
 <td class="tbody" align="right">0&nbsp;Kč</td>
 <td class="tbody" align="right">0</td>
 <td class="tbody" align="right"><input name="sell19628" class="numfield" maxlength="5" value="1" /><input type="button" class="ok" value="OK" onclick="javascript:notLogged();" /></td>
</tr>
         */
    }
}
