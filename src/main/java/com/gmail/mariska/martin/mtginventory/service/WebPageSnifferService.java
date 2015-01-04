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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.sniffer.CernyRytirLoader;
import com.gmail.mariska.martin.mtginventory.sniffer.ISniffer;
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
        BasicConfigurator.configure();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        WebPageSnifferService sniffer = new WebPageSnifferService(executor);
        Stopwatch stopky = Stopwatch.createStarted();
        List<DailyCardInfo> list = new ArrayList<>();

//        list.addAll(new TolarieLoader().sniffByEdition(CardEdition.BORN_OF_THE_GODS));

        list.addAll(sniffer.findCardsAtWeb(CardEdition.JOURNEY_INTO_NYX));
//        list.addAll(sniffer.findCardsAtWeb(CardEdition.THEROS, CardEdition.JOURNEY_INTO_NYX));
//        list.addAll(sniffer.findCardsAtWeb(CardEdition.THEROS));
//        list.addAll(sniffer.findCardsAtWeb("stifle", "soldier"));

        System.out.println(stopky.stop().elapsed(TimeUnit.MILLISECONDS));
        System.out.println(list);
        executor.shutdown();

    }

    private ExecutorService executor;
    private List<ISniffer> loaders;

    protected WebPageSnifferService(ExecutorService executor, List<ISniffer> loaders) {
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
            for (final ISniffer loader : loaders) {
                futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
                    @Override
                    public List<DailyCardInfo> call() throws Exception {
                        return loader.sniffByCardName(cardFindName);
                    }
                }));
            }
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

        for (final CardEdition cardEdition : editions) {
            if (logger.isTraceEnabled()) {
                logger.trace("Sniffing edition: " + cardEdition);
            }
            futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
                @Override
                public List<DailyCardInfo> call() throws Exception {
                    return new CernyRytirLoader().sniffByEdition(cardEdition);
                }
            }));
            futures.add(executor.submit(new Callable<List<DailyCardInfo>>() {
                @Override
                public List<DailyCardInfo> call() throws Exception {
                    return new TolarieLoader().sniffByEdition(cardEdition);
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

}
