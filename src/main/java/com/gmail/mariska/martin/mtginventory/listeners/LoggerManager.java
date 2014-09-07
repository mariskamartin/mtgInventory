package com.gmail.mariska.martin.mtginventory.listeners;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.gmail.mariska.martin.mtginventory.utils.Utils;

/**
 * Stara se o loggery
 * 
 * @author MAR
 */
public class LoggerManager implements ServletContextListener {
    private Logger logger;
    private static final String LOG_PROPERTIES_FILE = "log4j.properties";
    private static final String LOG_FILE = "/log/mtgInventory.log";

    @Override
    public void contextInitialized(ServletContextEvent e) {
        ServletContext ctx = e.getServletContext();
        System.setProperty("app.path", Utils.getDataDir(ctx));
        System.setProperty("app.log.file", getLogFile(ctx));
        PropertyConfigurator.configure(Utils.getDataDir(ctx) + LOG_PROPERTIES_FILE);

        logger = Logger.getLogger(LoggerManager.class.getName());
        logger.info("logger initialized at server date: " + new Date());
        logger.info("logger initialized at czech local date: " + Utils.createCzechDateTimeNow());
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {

    }

    public static String getLogFile(ServletContext ctx) {
        return Utils.getDataDir(ctx) + LOG_FILE;
    }
}
