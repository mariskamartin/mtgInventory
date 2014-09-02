package com.gmail.mariska.martin.mtginventory.auth;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;

import com.gmail.mariska.martin.mtginventory.db.model.User;

public class AuthenticationProvider {

    public User authenticateUser(ContainerRequestContext requestContext) {
        System.out.println(requestContext);
        for (String name : requestContext.getPropertyNames()) {
            System.out.println(name + " : " + requestContext.getProperty(name));
        }
        throw new NotAuthorizedException("Neni token");
    }

}
