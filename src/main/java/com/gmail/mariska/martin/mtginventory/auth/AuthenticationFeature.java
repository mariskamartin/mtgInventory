package com.gmail.mariska.martin.mtginventory.auth;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.gmail.mariska.martin.mtginventory.DatabaseManager;
import com.gmail.mariska.martin.mtginventory.service.AuthService;
import com.gmail.mariska.martin.mtginventory.service.UserService;

@Provider
public class AuthenticationFeature implements DynamicFeature {
    private AuthService authService;

    @Inject
    ServletContext ctx;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(AuthenticationRequired.class)) {
            AuthenticationFilter authenticationFilter = new AuthenticationFilter(getAuthProvider());
            context.register(authenticationFilter);
        }
    }

    private AuthService getAuthProvider() {
        if (authService == null) {
            authService = new AuthService(new UserService(DatabaseManager.getEM(ctx)));
        }
        return authService;
    }
}