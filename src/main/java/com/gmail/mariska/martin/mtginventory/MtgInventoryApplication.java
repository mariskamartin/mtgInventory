package com.gmail.mariska.martin.mtginventory;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.gmail.mariska.martin.mtginventory.auth.AuthenticationFeature;
import com.gmail.mariska.martin.mtginventory.auth.AuthenticationProvider;
import com.gmail.mariska.martin.mtginventory.rest.UserResource;

public class MtgInventoryApplication extends ResourceConfig {

    public MtgInventoryApplication() {
        // add json transformation
        super(JacksonFeature.class);
        // register all in rest package
        packages(UserResource.class.getPackage().getName());

        register(AuthenticationFeature.class);

        //register service for injections
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new AuthenticationProvider()).to(AuthenticationProvider.class);
//                // singleton binding
//                bind(MyInjectableSingleton.class).in(Singleton.class);
//                // singleton instance binding
//                bind(new MyInjectableSingleton()).to(MyInjectableSingleton.class);
            }
        });
    }
}