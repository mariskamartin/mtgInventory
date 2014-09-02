package com.gmail.mariska.martin.mtginventory.db.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

@Entity
@EntityListeners(JpaEntityTraceListener.class)
@XmlRootElement
public class User {

    @Id
    private String idEmail;
    private String name;
    private String password;
    private String token;

    public String getIdEmail() {
        return idEmail;
    }

    public void setIdEmail(String idEmail) {
        this.idEmail = idEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", idEmail)
                .add("name", name)
                .add("token", token)
                .toString();
    }
}
