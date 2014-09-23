package com.gmail.mariska.martin.mtginventory.listeners;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.service.AlertService;
import com.gmail.mariska.martin.mtginventory.service.EmailService;
import com.gmail.mariska.martin.mtginventory.service.ServiceFactory;
import com.google.common.eventbus.EventBus;

/**
 * Stara se o ostatni podpurne sluzby.
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
        startExecutorService(e.getServletContext());
        startEmailService(eventBus);
        startAlertService(eventBus, e.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        EventBus eventBus = Objects.requireNonNull(EventBusManager.getEventBus(e.getServletContext()),
                "EventBus neni inicializovan");
        stopAlertService(eventBus);
        stopEmailService(eventBus);
        stopExecutorService(e.getServletContext());
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

    private void startAlertService(EventBus eventBus, ServletContext ctx) {
        alertService = new AlertService(eventBus, ServiceFactory.createUserService(ctx),
                ServiceFactory.createCardService(ctx), ServiceFactory.createUrlService(ctx));
        eventBus.register(alertService);
        logger.info("Alerts service created");
    }

    private void stopAlertService(EventBus eventBus) {
        eventBus.unregister(alertService);
        logger.info("Alerts service destoryed");
    }

    private final static String EXECUTOR_SERVICE = "service.executor";
    private final int NUM_THREADS = 10;

    /**
     * Returns instance of servlet ExecutorService
     * 
     * @param ctx
     *            main servlet context
     * @return
     */
    public static ExecutorService getExecutorService(ServletContext ctx) {
        return (ExecutorService) ctx.getAttribute(EXECUTOR_SERVICE);
    }

    private void startExecutorService(ServletContext ctx) {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        ctx.setAttribute(EXECUTOR_SERVICE, executorService);
        logger.info("Executor service created");
    }

    private void stopExecutorService(ServletContext ctx) {
        ExecutorService executorService = getExecutorService(ctx);
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
        }
        ctx.removeAttribute(EXECUTOR_SERVICE);
        logger.info("Executor service destoryed");
    }
}
