package com.gmail.mariska.martin.mtginventory.service;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import com.gmail.mariska.martin.mtginventory.db.BannedCardNamesDao;
import com.gmail.mariska.martin.mtginventory.db.UserCardsDao;
import com.gmail.mariska.martin.mtginventory.db.UserDao;
import com.gmail.mariska.martin.mtginventory.db.validators.CardDaoValidated;
import com.gmail.mariska.martin.mtginventory.listeners.DatabaseManager;
import com.gmail.mariska.martin.mtginventory.listeners.EventBusManager;
import com.gmail.mariska.martin.mtginventory.listeners.SupportServiciesManager;

/**
 * Vyrabi vsechny dulezite servisy
 * 
 * @author MAR
 * 
 */
public class ServiceFactory {

    /**
     * Karty
     * 
     * @param ctx
     * @return
     */
    public static CardService createCardService(ServletContext ctx) {
        EntityManager em = DatabaseManager.getEM(ctx);
        BannedCardNamesDao bannedDao = new BannedCardNamesDao(em);
        return new CardService(em,
                EventBusManager.getEventBus(ctx),
                createWebPageSnifferService(ctx),
                new CardDaoValidated(em, bannedDao),
                bannedDao);
    }

    /**
     * Uzivatel
     * 
     * @param context
     * @return
     */
    public static UserService createUserService(ServletContext context) {
        EntityManager em = DatabaseManager.getEM(context);
        return new UserService(em, new UserDao(em));
    }

    /**
     * Karty uzivatele
     * 
     * @param context
     * @return
     */
    public static UserCardsService createUserCardsService(ServletContext context) {
        EntityManager em = DatabaseManager.getEM(context);
        return new UserCardsService(em, new UserCardsDao(em));
    }

    /**
     * Stahovani karet z webu
     * 
     * @param context
     * @return
     */
    public static WebPageSnifferService createWebPageSnifferService(ServletContext context) {
        return new WebPageSnifferService(SupportServiciesManager.getExecutorService(context));
    }

    /**
     * Service pro generovani ruznych URL
     * @param context
     * @return
     */
    public static UrlService createUrlService(ServletContext context) {
        return new UrlService(context);
    }


}
