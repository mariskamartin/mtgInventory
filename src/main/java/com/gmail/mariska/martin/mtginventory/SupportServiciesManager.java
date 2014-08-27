package com.gmail.mariska.martin.mtginventory;

import java.util.Objects;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.service.AlertService;
import com.gmail.mariska.martin.mtginventory.service.EmailService;
import com.google.common.eventbus.EventBus;

/**
 * Stara se o Emaily.
 * 
 * @author MAR
 */
public class SupportServiciesManager implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(SupportServiciesManager.class.getName());
    private EmailService emailService;
    private AlertService alertService;

    @Override
    public void contextInitialized(ServletContextEvent e) {
        EventBus eventBus = Objects.requireNonNull(EventBusManager.getEventBus(e.getServletContext()),
                "EventBus neni inicializovan");
        startEmailService(eventBus);
        startAlertService(eventBus);
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        EventBus eventBus = Objects.requireNonNull(EventBusManager.getEventBus(e.getServletContext()),
                "EventBus neni inicializovan");
        stopAlertService(eventBus);
        stopEmailService(eventBus);
    }

    private void startEmailService(EventBus eventBus) {
        emailService = new EmailService();
        eventBus.register(emailService);
        logger.info("Email service created");
    }

    private void stopEmailService(EventBus eventBus) {
        eventBus.unregister(emailService);
        logger.info("Email service destoryed");
    }

    private void startAlertService(EventBus eventBus) {
        alertService = new AlertService(eventBus);
        eventBus.register(alertService);
        logger.info("Alerts service created");
    }

    private void stopAlertService(EventBus eventBus) {
        eventBus.unregister(alertService);
        logger.info("Alerts service destoryed");
    }

}
