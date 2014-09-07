package com.gmail.mariska.martin.mtginventory.rest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.StreamingOutput;

import com.gmail.mariska.martin.mtginventory.listeners.LoggerManager;
import com.google.common.io.Files;

@Path("/log")
public class LogResource {

    @Context
    ServletContext context;

    @GET
    @Produces({"text/plain"})
    public StreamingOutput getLogFile() throws Exception {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    Files.copy(new File(LoggerManager.getLogFile(context)), output);
                } catch (Exception e) {
                    throw new InternalServerErrorException(e);
                }
            }
        };
    }
}
