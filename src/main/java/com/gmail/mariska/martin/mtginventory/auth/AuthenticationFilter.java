package com.gmail.mariska.martin.mtginventory.auth;

import java.io.IOException;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.apache.log4j.Logger;

import com.gmail.mariska.martin.mtginventory.db.model.User;
import com.gmail.mariska.martin.mtginventory.service.AuthService;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class);
    private static final String PROPERTY = "auth.user";
    private AuthService authenticationProvider;

    public AuthenticationFilter(AuthService authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> tokens = requestContext.getHeaders().get("X-Auth-Token");
        if (logger.isDebugEnabled()) {
            logger.debug("auth filter > accepted token : " + tokens.get(0));
        }
        User authenticateUser = this.authenticationProvider.verify(tokens.get(0));
        if (authenticateUser == null) {
            throw new NotAuthorizedException("Wrong token");
        }
        requestContext.setProperty(PROPERTY, authenticateUser);
    }
}