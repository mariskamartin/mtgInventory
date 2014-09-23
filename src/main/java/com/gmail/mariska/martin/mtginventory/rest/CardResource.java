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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.auth.AuthenticationRequired;
import com.gmail.mariska.martin.mtginventory.db.model.BannedCardNames;
import com.gmail.mariska.martin.mtginventory.db.model.Card;
import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovement;
import com.gmail.mariska.martin.mtginventory.db.model.CardMovementType;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;
import com.gmail.mariska.martin.mtginventory.db.model.UsersCards;
import com.gmail.mariska.martin.mtginventory.listeners.EventBusManager;
import com.gmail.mariska.martin.mtginventory.service.AlertService.GenerateUserEmailsAlertEvent;
import com.gmail.mariska.martin.mtginventory.service.CardService;
import com.gmail.mariska.martin.mtginventory.service.EmailService.EmailMessage;
import com.gmail.mariska.martin.mtginventory.service.ServiceFactory;
import com.google.common.eventbus.EventBus;

@Path("/cards")
public class CardResource {
    private static final Logger logger = Logger.getLogger(CardResource.class);

    @Context
    ServletContext context;

    @AuthenticationRequired
    @GET
    @Path("/user/{userId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Card> getCards(@PathParam("userId") String userId) {
//        return ServiceFactory.createUserCardsService(context).getAllUsersCards(userId);
        return ServiceFactory.createCardService(context).getCardsByUser(userId);
    }

    @AuthenticationRequired
    @POST
    @Path("/user/{userId}/add/{cardId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public UsersCards addUserCard(@PathParam("userId") String userId, @PathParam("cardId") String cardId) {
        return ServiceFactory.createUserCardsService(context).addUserCard(userId, cardId);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Card> getCards() {
        return ServiceFactory.createCardService(context).getAll();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{id}")
    public Card getCardById(@PathParam("id") String id) {
        return ServiceFactory.createCardService(context).findById(id);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("find/{text}")
    public Collection<Card> findCardsByText(@PathParam("text") String text) {
        return ServiceFactory.createCardService(context).findsCards(text);
    }

// @AuthenticationRequired
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/generate/{action}")
    public String generatePriceMovement(@PathParam("action") String action,
                                        @QueryParam("edition") String edition,
                                        @QueryParam("rarity") String rarityCrKey) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("generate action: " + action);
        }
        EventBus eventBus = EventBusManager.getEventBus(context);

        if (action.equals("movement")) {
            CardService cardService = ServiceFactory.createCardService(context);
            Date now = new Date();
            cardService.deleteCardMovementByType(CardMovementType.DAY);
            cardService.generateCardsMovements(now, CardMovementType.DAY);
            cardService.deleteCardMovementByType(CardMovementType.START_OF_WEEK);
            cardService.generateCardsMovements(now, CardMovementType.START_OF_WEEK);
        } else if (action.equals("useremails")) {
            eventBus.post(new GenerateUserEmailsAlertEvent());
        } else if (action.equals("fetchedition")) {
            // /MtgInventory/rest/v1.0/cards/generate/fetchedition?edition=MAGIC_2015&rarity=M
            CardService cardService = ServiceFactory.createCardService(context);
            cardService.fetchCardsByEditionRarityOnCR(CardEdition.valueOf(edition), rarityCrKey);
        } else if (action.equals("testemail")) {
            eventBus.post(new EmailMessage.Builder().testMsg().build());
        }

        return null;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/fetch/{name}")
    public Collection<Card> fetchCardById(@PathParam("name") String name) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Fetching new card: " + name);
        }
        return ServiceFactory.createCardService(context).fetchCards(name);
    }

    @AuthenticationRequired
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/fetch/")
    public Collection<Card> fetchAllCard() throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("User invoked fetching");
        }
        return ServiceFactory.createCardService(context).fetchAllManagedCards();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/dailyinfo/{id}")
    public List<DailyCardInfo> getDailyCardInfoById(@PathParam("id") String id) {
        return ServiceFactory.createCardService(context).getDailyInfo(id);
    }

    /**
     * Vrati pohyby karet pro zadany typ, k zobrazeni. Setridene od nejvetsi zmeny
     * 
     * @param type
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/movements/{type}")
    public List<CardMovement> getCardMovementsByType(@PathParam("type") CardMovementType type) {
        return ServiceFactory.createCardService(context).getCardMovementsByType(type);
    }

    @AuthenticationRequired
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/ban/")
    public BannedCardNames banCardName(BannedCardNames bannedCard) throws IOException {
        return ServiceFactory.createCardService(context).addBannedCardName(bannedCard);
    }

    @AuthenticationRequired
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Card insert(Card newEnt) throws IOException {
        return ServiceFactory.createCardService(context).insert(newEnt);
    }

    @AuthenticationRequired
    @DELETE
    @Path("/{id}")
    public Card delete(@PathParam("id") String id) {
        return ServiceFactory.createCardService(context).delete(id);
    }
}
