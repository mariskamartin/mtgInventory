package com.gmail.mariska.martin.mtginventory.db.model;


import javax.jdo.annotations.Unique;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

@SuppressWarnings({"JpaDataSourceORMInspection", "UnusedDeclaration"})
@XmlRootElement
@JsonIgnoreProperties(value = { "isAdmin" })
@Entity
@EntityListeners(JpaEntityTraceListener.class)
@Table(
    uniqueConstraints=@UniqueConstraint(name="uk_token", columnNames={"token"})
)
@Unique(name="uk_token", members={"token"})
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
    @XmlTransient
    @JsonIgnore
    private String password;
    private String token;
    private String roles;

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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String role) {
        this.roles = role;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

//    public String getRoles() {
//        return Sets.newCopyOnWriteArraySet(roles);
//    }
//
//    public void setRoles(String role) {
//        this.roles = Sets.newLinkedHashSet(role);
//    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", idEmail)
                .add("name", name)
                .add("token", token)
                .add("roles", roles)
                .toString();
    }
}
