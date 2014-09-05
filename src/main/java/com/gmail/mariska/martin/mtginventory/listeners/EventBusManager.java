package com.gmail.mariska.martin.mtginventory.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Stara se o EventBus.
 * 
 * @author MAR
 */
public class EventBusManager implements ServletContextListener {
    private static final String MAIN_EVENT_BUS = "mainEventBus";
    private static final Logger logger = Logger.getLogger(EventBusManager.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent e) {
        EventBus eventBus = new EventBus("MgtInvetoryMainEventBus");
        trackDeathEventsInDebugMode(eventBus);
        e.getServletContext().setAttribute(MAIN_EVENT_BUS, eventBus);
        logger.info("EventBus created");
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        logger.info("EventBus destoryed");
    }

    /**
     * return global application eventBus
     * 
     * @param ctx
     * @return eventBus
     */
    public static EventBus getEventBus(ServletContext ctx) {
        return (EventBus) ctx.getAttribute(MAIN_EVENT_BUS);
    }

    /**
     * Turn on tracking of death events in debbug mode
     * 
     * @param eventBus
     */
    private void trackDeathEventsInDebugMode(EventBus eventBus) {
        eventBus.register(new Object() {
            @Subscribe
            public void collectDeathMsgs(DeadEvent e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("DeadEvent : " + e.getEvent() + " sent by : " + e.getSource());
                }
            }
        });
    }
}
