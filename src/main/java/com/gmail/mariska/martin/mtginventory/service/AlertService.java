package com.gmail.mariska.martin.mtginventory.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.model.CardMovement;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.db.model.User;
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
    private final UserService userService;
    private final CardService cardService;
    private final UrlService urlService;

    public AlertService(EventBus eventBus, UserService userService, CardService cardService, UrlService urlService) {
        this.eventBus = eventBus;
        this.cardService = cardService;
        this.userService = userService;
        this.urlService = urlService;
    }

// @Subscribe
// public void acceptEvent(AlertEvent alert) {
// // pro vsechny alerty zalogovat
// logger.debug("accept alert event " + alert);
// }

    @Subscribe
    public void acceptMovementEvent(MovementAlertEvent alert) {
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
    public void acceptGenerateUsersEmailsEvent(GenerateUserEmailsAlertEvent alert) {
        List<User> users = userService.getAll();
        for (User user : users) {
            StringBuilder content = new StringBuilder();
            Collection<CardMovement> um = cardService.getUsersMovements(user.getIdEmail());
            content.append("<h1><a href=\""+ urlService.getInterestsUrl() +"\">Mtg Inventory</a></h1>");
            content.append("<h2>Změny u sledovaných karet</h2>");
            for (CardMovement ucm : um) {
                content.append("<li>" + ucm.getCard().getName() + (ucm.getCard().isFoil() ? " FOIL" : "") + " - změna ceny z " + ucm.getOldPrice() + " na "
                        + ucm.getNewPrice() + " (" + ucm.getGainPercentage() + " %) "+ ucm.getShop() +"</li>");
            }

            Collection<DailyCardInfo> udci = cardService.getUsersDailyCardInfo(user.getIdEmail());
            content.append("<h2>Počty karet na skladě jednotlivých obchodů</h2>");
            for (DailyCardInfo userDci : udci) {
                content.append("<li>" + userDci.getCard().getName()+ (userDci.getCard().isFoil() ? " FOIL" : "") + " - " + userDci.getShop().name() + " - "
                        + userDci.getStoreAmount() + " Ks (cena " + userDci.getPrice() +")</li>");
            }

            if (content.length() > 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("try to send alert email to " + user.getIdEmail());
                }
                eventBus.post(new EmailMessage.Builder().setRecipient(user.getIdEmail())
                        .setSubject("User's card information")
                        .setContent(content.toString())
                        .build());
            }
        }
    }

    @Subscribe
    public void acceptGenerateEvent(GenerateAlertEvent alert) {
        if (logger.isDebugEnabled()) {
            logger.debug("try to send email " + alert);
        }
        eventBus.post(new EmailMessage.Builder().setRecMar().
                setSubject("Alert Event").
                setContent("<h1>Alert</h1><p>" + alert.getReason() + "</p>").
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
     * Pro pohyby daily info
     * 
     * @author MAR
     */
    public static class DailyCardInfoFetchedAlertEvent extends AlertEvent {
        private List<DailyCardInfo> dailyCardInfo;

        public DailyCardInfoFetchedAlertEvent(List<DailyCardInfo> dcis) {
            this.dailyCardInfo = dcis;
        }

        public List<DailyCardInfo> getDailyCardInfoList() {
            return dailyCardInfo;
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

    /**
     * Pouze pro testovani ma iniciaovat generovani uzivatelskych emailu s jejich kartami
     * @author MAR
     *
     */
    public static class GenerateUserEmailsAlertEvent extends AlertEvent {
    }
}
