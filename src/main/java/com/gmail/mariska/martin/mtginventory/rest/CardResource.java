package com.gmail.mariska.martin.mtginventory.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.DatabaseManager;
import com.gmail.mariska.martin.mtginventory.EventBusManager;
import com.gmail.mariska.martin.mtginventory.auth.AuthenticationRequired;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovement;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovementType;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.service.CardService;
import com.gmail.mariska.martin.mtginventory.service.EmailService.EmailMessage;
import com.google.common.eventbus.EventBus;

@Path("/cards")
public class CardResource {
    private static final Logger logger = Logger.getLogger(CardResource.class);

    @Context
    ServletContext context;

    @AuthenticationRequired
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Card> getCards() {
        CardService userService = new CardService(DatabaseManager.getEM(context));
        return userService.getAllWithoutFoil();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{id}")
    public Card getCardById(@PathParam("id") String id) {
        CardService userService = new CardService(DatabaseManager.getEM(context));
        return userService.findById(id);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/generate/{action}")
    public String generatePriceMovement(@PathParam("action") String action) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("generate action: " + action);
        }
        EventBus eventBus = EventBusManager.getEventBus(context);

        if (action.equals("movement")) {
            CardService cardService = new CardService(DatabaseManager.getEM(context), eventBus);
            Date now = new Date();
            cardService.deleteCardMovementByType(CardMovementType.DAY);
            cardService.generateCardsMovements(now, CardMovementType.DAY);
            cardService.deleteCardMovementByType(CardMovementType.START_OF_WEEK);
            cardService.generateCardsMovements(now, CardMovementType.START_OF_WEEK);
        } else if(action.equals("testemail")) {
            eventBus.post(new EmailMessage.Builder().testMsg().build());
        }

        return null;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/fetch/{name}")
    public Collection<Card> fetchCardById(@PathParam("name") String name) throws IOException {
        CardService userService = new CardService(DatabaseManager.getEM(context));
        if (logger.isDebugEnabled()) {
            logger.debug("Fetching new card: " + name);
        }
        return userService.fetchCards(name);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/fetch/")
    public Collection<Card> fetchAllCard() throws IOException {
        CardService userService = new CardService(DatabaseManager.getEM(context), EventBusManager.getEventBus(context));
        if (logger.isDebugEnabled()) {
            logger.debug("User invoked fetching");
        }
        return userService.fetchAllManagedCards();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/dailyinfo/{id}")
    public List<DailyCardInfo> getDailyCardInfoById(@PathParam("id") String id) {
        CardService userService = new CardService(DatabaseManager.getEM(context));
        return userService.getDailyInfo(id);
    }

    /**
     * Vrati pohyby karet pro zadany typ, k zobrazeni. Setridene od nejvetsi zmeny
     * @param type
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/movements/{type}")
    public List<CardMovement> getCardMovementsByType(@PathParam("type") CardMovementType type) {
        return new CardService(DatabaseManager.getEM(context)).getCardMovementsByType(type);
    }

    @AuthenticationRequired
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Card insert(Card newEnt) throws IOException {
        CardService userService = new CardService(DatabaseManager.getEM(context));
        return userService.insert(newEnt);
    }

    @AuthenticationRequired
    @DELETE
    @Path("/{id}")
    public Card delete(@PathParam("id") String id) {
        CardService userService = new CardService(DatabaseManager.getEM(context));
        return userService.delete(id);
    }
}
