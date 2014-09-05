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
    private static final String LOGGER_FILE = "log4j.properties";

    @Override
    public void contextInitialized(ServletContextEvent e) {
        ServletContext ctx = e.getServletContext();
        String data_dir = Utils.getDataDir(ctx);
        System.setProperty("app.path", data_dir);
        PropertyConfigurator.configure(data_dir + LOGGER_FILE);

        logger = Logger.getLogger(LoggerManager.class.getName());
        logger.info("logger initialized at date: " + new Date());
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {

    }
}
