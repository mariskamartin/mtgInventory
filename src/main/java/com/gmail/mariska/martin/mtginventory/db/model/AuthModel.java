package com.gmail.mariska.martin.mtginventory.db.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model slouzici pro authorizaci a autentizaci
 * @author MAR
 *
 */
@XmlRootElement
public class AuthModel {
    private String loginEmail;
    private String password;
    private String token;

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
