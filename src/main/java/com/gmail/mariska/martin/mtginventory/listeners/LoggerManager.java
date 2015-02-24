package com.gmail.mariska.martin.mtginventory.listeners;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String LOG_PROPERTIES_FILE = "log4j-local.properties";
    private static final String LOG_PROPERTIES_FILE_ONLINE = "log4j-online.properties";
    private static final String LOG_FILE = "log" + File.separator +"mtgInventory.log";

    @Override
    public void contextInitialized(ServletContextEvent e) {
        ServletContext ctx = e.getServletContext();
        System.setProperty("app.path", Utils.getWebInfDir(ctx));
        System.setProperty("app.log.file", getLogFile(ctx));

        PropertyConfigurator.configure(this.getClass().getClassLoader().getResourceAsStream(getLogPorpertiesFilename(ctx)));

        Logger logger = Logger.getLogger(LoggerManager.class.getName());
        logger.info("logger initialized at server date: " + new Date());
        logger.info("logger initialized at czech local date: " + Utils.createCzechDateTimeNow());
        logger.info("logger properties from: " + getLogPorpertiesFilename(ctx));
        logger.info("log file: " + getLogFile(ctx));
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {

    }

    public static String getLogFile(ServletContext ctx) {
        return Utils.getDataDir(ctx) + LOG_FILE;
    }

    private String getLogPorpertiesFilename(ServletContext ctx) {
        return Utils.isOpenshift() ? LOG_PROPERTIES_FILE_ONLINE : LOG_PROPERTIES_FILE;
    }

}
