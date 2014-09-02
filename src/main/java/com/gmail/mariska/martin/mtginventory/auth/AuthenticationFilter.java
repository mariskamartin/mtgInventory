package com.gmail.mariska.martin.mtginventory.auth;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import com.gmail.mariska.martin.mtginventory.db.model.User;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String PROPERTY = "auth.user";
    private final AuthenticationProvider authenticationProvider;

    public AuthenticationFilter(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        User authenticateUser = this.authenticationProvider.authenticateUser(requestContext);
        requestContext.setProperty(PROPERTY, authenticateUser);
    }
}