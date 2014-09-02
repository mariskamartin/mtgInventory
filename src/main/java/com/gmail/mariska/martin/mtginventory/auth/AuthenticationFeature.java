package com.gmail.mariska.martin.mtginventory.auth;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFeature implements DynamicFeature {

    @Inject
    AuthenticationProvider authenticationProvider;

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(AuthenticationRequired.class)) {
            AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationProvider);
            context.register(authenticationFilter);
        }
    }
}