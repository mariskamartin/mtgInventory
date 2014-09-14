package com.gmail.mariska.martin.mtginventory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger logger = Logger.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
            Response origResponse = ((WebApplicationException) ex).getResponse();
            return Response.fromResponse(origResponse)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(ex.getMessage())
                    .build();
        } else {
            logger.error(ex.getMessage(), ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

}
