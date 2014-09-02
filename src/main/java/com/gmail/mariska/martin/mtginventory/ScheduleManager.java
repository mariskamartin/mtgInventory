package com.gmail.mariska.martin.mtginventory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.gmail.mariska.martin.mtginventory.db.model.CardMovementType;
import com.gmail.mariska.martin.mtginventory.service.AlertService;
import com.gmail.mariska.martin.mtginventory.service.CardService;
import com.google.common.eventbus.EventBus;

/**
 * Stara se o zapnuti vypnuti schedule service.
 * 
 * @author MAR
 */
public class ScheduleManager implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(ScheduleManager.class.getName());
    private static final int SCHEDULE_PERIOD = 12 * 60;
    private static final int SCHEDULE_START = 6;
    private static final DateTimeZone czechTZ = DateTimeZone.forID("Europe/Prague");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<ScheduledFuture<?>> scheduledList = new LinkedList<ScheduledFuture<?>>();
    private ServletContext ctx;

    @Override
    public void contextInitialized(ServletContextEvent e) {
        ctx = e.getServletContext();
        scheduleDailyManagedCardControl();
        logger.info("scheduler started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        terminateTasks();
        logger.info("scheduler stopped");
    }

    private void terminateTasks() {
        for (ScheduledFuture<?> schFutrs : scheduledList) {
            schFutrs.cancel(true);
        }
        scheduler.shutdownNow();
        try {
            scheduler.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("all tasks terminated = " + scheduler.isTerminated());
        }
    }

    /**
     * Naplanuje akci kdy se jednou denne stahnou data pro managed entity
     */
    private void scheduleDailyManagedCardControl() {
        ScheduledFuture<?> scheduleAtFixedRate = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                EventBus eventBus = EventBusManager.getEventBus(ctx);
                logger.info("start uploading information about managed cards");
                CardService cardService = new CardService(DatabaseManager.getEM(ctx), eventBus);
                cardService.fetchAllManagedCards();
                logger.info("end of uploading information about managed cards");
                logger.info("start update card movements");
                cardService.deleteCardMovementByType(CardMovementType.DAY);
                cardService.generateCardsMovements(new Date(), CardMovementType.DAY);
                cardService.deleteCardMovementByType(CardMovementType.START_OF_WEEK);
                cardService.generateCardsMovements(new Date(), CardMovementType.START_OF_WEEK);
                logger.info("end update card movements");

                eventBus.post(
                        new AlertService.GenerateAlertEvent(
                                "automaticky spustene stahovani karet a pregenerovani movements"));
            }
        }, getMinutesToScheduledStart(), SCHEDULE_PERIOD, TimeUnit.MINUTES);
        scheduledList.add(scheduleAtFixedRate);
    }

    private long getMinutesToScheduledStart() {
        DateTime start = DateTime.now(czechTZ).withHourOfDay(SCHEDULE_START).withMinuteOfHour(0);
        Duration duration = new Duration(DateTime.now(czechTZ), start);
        while(duration.getStandardMinutes() < 0) {
            start = start.plusHours(12);
            duration = new Duration(DateTime.now(czechTZ), start);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("minutes to start scheduled tasks: " + duration.getStandardMinutes());
        }
        return duration.getStandardMinutes();
    }
}
