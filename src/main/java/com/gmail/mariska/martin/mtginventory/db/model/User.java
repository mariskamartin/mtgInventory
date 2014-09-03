package com.gmail.mariska.martin.mtginventory.db.model;

import javax.jdo.annotations.Unique;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

@Entity
@EntityListeners(JpaEntityTraceListener.class)
@XmlRootElement
@Unique(members={"token"})
public class User {
    /**
     * As one place for META names
     */
    public static enum PROPS {
        idEmail, name, token, password, version
    }

    @Id
    private String idEmail;
    @Version
    private long version;
    private String name;
    private String password;
    private String token;

    public String getIdEmail() {
        return idEmail;
    }

    public void setIdEmail(String idEmail) {
        this.idEmail = idEmail;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
                .add("version", version)
                .toString();
    }
}
