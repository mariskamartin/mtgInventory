package com.gmail.mariska.martin.mtginventory;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.gmail.mariska.martin.mtginventory.rest.UserResource;

public class MtgInventoryApplication extends ResourceConfig {

    public MtgInventoryApplication() {
        // add json transformation
        super(JacksonFeature.class);
        // register all in rest package
        packages(UserResource.class.getPackage().getName());

        //register service for injections
//        register(new AbstractBinder() {
//            @Override
//            protected void configure() {
//                // singleton binding
//                bind(MyInjectableSingleton.class).in(Singleton.class);
//                // singleton instance binding
//                bind(new MyInjectableSingleton()).to(MyInjectableSingleton.class);
//
//            }
//        });
    }
}