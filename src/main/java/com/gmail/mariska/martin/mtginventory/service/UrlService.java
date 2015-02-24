package com.gmail.mariska.martin.mtginventory.service;

import javax.servlet.ServletContext;

public class UrlService {
    private static final String BASE_APP_URL = "http://todoapp-mariskamartin.rhcloud.com/MtgInventory/";

    public UrlService(ServletContext context) {
        //
    }

    public String getApplicationUrl() {
        return BASE_APP_URL;
    }

    public String getInterestsUrl() {
        return BASE_APP_URL + "#/interests";
    }
}
