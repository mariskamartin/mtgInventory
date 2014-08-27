package com.gmail.mariska.martin.mtginventory.service;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.model.CardMovement;
import com.gmail.mariska.martin.mtginventory.service.EmailService.EmailMessage;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Logika pro vyrabeni alertu > email a dal
 * 
 * @author MAR
 * 
 */
public class AlertService {
    private final Logger logger = Logger.getLogger(AlertService.class);
    private EventBus eventBus;

    public AlertService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

// @Subscribe
// public void acceptEvent(AlertEvent alert) {
// // pro vsechny alerty zalogovat
// logger.debug("accept alert event " + alert);
// }

    @Subscribe
    public void acceptMovementEvent(MovementAlertEvent alert) {
        // odesilat pouze denni alerty
// if (alert.getCardMovement().getType().equals(CardMovementType.DAY)) {
// logger.debug("try to send alert email " + alert);
// String content = "<p>Dnes se změnila cena sledované karty <i>"
// + alert.getCardMovement().getCard().getName() + "</i> o "
// + alert.getCardMovement().getGainPercentage() + " % v obchode " + alert.getCardMovement().getShop()
// + "</p>";
// eventBus.post(new EmailMessage("mariska.martin@gmail.com", "Change of card price", content));
// }
    }

    @Subscribe
    public void acceptGenerateEvent(GenerateAlertEvent alert) {
        // odesilat pouze denni alerty
        if (logger.isDebugEnabled()) {
            logger.debug("try to send email " + alert);
        }
        eventBus.post(new EmailMessage.Builder().setRecMar().
                setSubject("Auto generate action").
                setContent("<h1>Generování obsahu</h1><p>" + alert.getReason() + "</p>").
                build());
    }

    /**
     * Predek pro vsechny alerty
     * 
     * @author MAR
     * 
     */
    public static class AlertEvent {
    }

    /**
     * Pro pohyby karet
     * 
     * @author MAR
     */
    public static class MovementAlertEvent extends AlertEvent {
        private CardMovement cardMovement;

        public MovementAlertEvent(CardMovement cm) {
            cardMovement = cm;
        }

        public CardMovement getCardMovement() {
            return cardMovement;
        }
    }

    /**
     * pro generovane akce
     * 
     * @author MAR
     */
    public static class GenerateAlertEvent extends AlertEvent {
        private String reason;

        public GenerateAlertEvent(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }

}
