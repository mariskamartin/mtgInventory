package com.gmail.mariska.martin.mtginventory.rest;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.listeners.DatabaseManager;

@Path("/admin")
public class AdminResource {
    private static final Logger logger = Logger.getLogger(AdminResource.class);

    @Context
    ServletContext context;

// @AuthenticationRequired
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("/{consoleText}")
    public List generatePriceMovement(@PathParam("consoleText") String consoleText) throws IOException {
        EntityManager em = DatabaseManager.getEM(context);
        return em.createQuery(consoleText).getResultList();
    }

}
