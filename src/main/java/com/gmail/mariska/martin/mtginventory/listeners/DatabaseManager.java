package com.gmail.mariska.martin.mtginventory.listeners;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.CardDao;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardRarity;
import com.gmail.mariska.martin.mtginventory.service.CardService;
import com.gmail.mariska.martin.mtginventory.utils.Utils;
import com.google.common.collect.Lists;

/**
 * Stara se o databazi a poskytuje EM.
 * 
 * @author MAR
 */
public class DatabaseManager implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static final String OBJECTDB_FILE = "db/mtgInventory.odb";

    @Override
    public void contextInitialized(ServletContextEvent e) {
        String data_dir = Utils.getDataDir(e.getServletContext());
        com.objectdb.Enhancer.enhance(Card.class.getPackage().getName()+".*");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(data_dir+OBJECTDB_FILE);
        e.getServletContext().setAttribute("emf", emf);
        logger.info("database started");

        correctDatabase(e.getServletContext());
    }

    private void correctDatabase(ServletContext servletContext) {
        logger.info("Correct database - unknown cards");
        EntityManager em = getEM(servletContext);
        CardDao cardDao = new CardDao(em);
        CardService cardService = new CardService(em);

        List<Card> forDeleteList = Lists.newArrayList();
        List<Card> deletedList = Lists.newArrayList();
        forDeleteList.addAll(cardDao.findByEdition(CardEdition.UNKNOWN));
        forDeleteList.addAll(cardDao.findByRarity(CardRarity.UNKNOWN));
        for (Card card : forDeleteList) {
            Card deleted = cardService.delete(card.getId());
            if (deleted != null) {
                deletedList.add(deleted);
            }
        }
        logger.info("Deleted cards " + forDeleteList.size() + " / " + deletedList.size());
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        EntityManagerFactory emf = (EntityManagerFactory)e.getServletContext().getAttribute("emf");
        emf.close();
        logger.info("database closed");
    }

    /**
     * Slouzi pro vyndani zpet EM
     * @param ctx
     * @return em
     */
    public static EntityManager getEM(ServletContext ctx) {
        return ((EntityManagerFactory) ctx.getAttribute("emf")).createEntityManager();
    }
}
