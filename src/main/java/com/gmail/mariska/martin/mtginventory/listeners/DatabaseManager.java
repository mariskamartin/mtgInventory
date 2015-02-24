package com.gmail.mariska.martin.mtginventory.listeners;

import com.gmail.mariska.martin.mtginventory.db.BannedCardNamesDao;
import com.gmail.mariska.martin.mtginventory.db.CardDao;
import com.gmail.mariska.martin.mtginventory.db.DailyCardInfoDao;
import com.gmail.mariska.martin.mtginventory.db.model.*;
import com.gmail.mariska.martin.mtginventory.service.CardService;
import com.gmail.mariska.martin.mtginventory.utils.Utils;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * Stara se o databazi a poskytuje EM.
 *
 * @author MAR
 */
public class DatabaseManager implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static final String OBJECTDB_FILE = "db/mtgi.odb";
    private static final String OBJECTDB_FILE_OLD = "db/mtgInventory.odb";

    @Override
    public void contextInitialized(ServletContextEvent e) {
        //register entity
//        com.objectdb.Enhancer.enhance(Card.class.getPackage().getName() + ".*"); //all
        com.objectdb.Enhancer.enhance(Card.class.getName());
        com.objectdb.Enhancer.enhance(CardMovement.class.getName());
        com.objectdb.Enhancer.enhance(DailyCardInfo.class.getName());
        com.objectdb.Enhancer.enhance(User.class.getName());
        com.objectdb.Enhancer.enhance(UsersCards.class.getName());
        com.objectdb.Enhancer.enhance(BannedCardNames.class.getName());

        //lze si nastavit systemovou promenou MTGI_DATA_DIR, ktera ukazuje stale na stejny soubor s databazi... jinak se vytvori v aplikaci a ta se muze mazat
        String persistenceUnitName = Utils.getDataDir(e.getServletContext()) + OBJECTDB_FILE;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        e.getServletContext().setAttribute("emf", emf);

        logger.info("database started with: " + persistenceUnitName);

//        importFormOldDatabase(e.getServletContext());
    }

    private void importFormOldDatabase(ServletContext ctx) {
        String persistenceFile = Utils.getDataDir(ctx) + OBJECTDB_FILE_OLD;
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityManager targetEM = getEM(ctx);
        try {
            logger.info("Start importing old data from " + persistenceFile);
            emf = Persistence.createEntityManagerFactory(persistenceFile);
            em = emf.createEntityManager();

            //load Dci and save to DB
            CardService cardService = new CardService(targetEM, null, null, new CardDao(targetEM), new BannedCardNamesDao(targetEM));
            DailyCardInfoDao dao = new DailyCardInfoDao(em);

            int max = 5000;
            int pageSize = 100;
            int startIndex = 0;
            List<DailyCardInfo> all = dao.limit(pageSize).getAll();
            while(all.size() > 0 && startIndex < max){
                for(DailyCardInfo dc : all){
                    em.detach(dc);
                    em.detach(dc.getCard());
                    dc.setId(null);
                    dc.getCard().setId(null);
                }
                cardService.saveCardsIntoDb(all);
                logger.info("importing old data - " + startIndex + " / unknown");

                startIndex += pageSize;
                all = dao.limit(pageSize).startAt(startIndex).getAll();
            }
            logger.info("End of importing old data from " + persistenceFile);
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent e) {
        EntityManagerFactory emf = (EntityManagerFactory) e.getServletContext().getAttribute("emf");
        emf.close();
        logger.info("database closed");
    }

    /**
     * Slouzi pro vyndani zpet EM
     *
     * @param ctx servlet context
     * @return em EntityManager
     */
    public static EntityManager getEM(ServletContext ctx) {
        return ((EntityManagerFactory) ctx.getAttribute("emf")).createEntityManager();
    }
}
