package com.gmail.mariska.martin.mtginventory.listeners;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.utils.Utils;

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
