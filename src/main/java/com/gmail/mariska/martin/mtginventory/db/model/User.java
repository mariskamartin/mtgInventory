package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Set;

import javax.jdo.annotations.Unique;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

@Entity
@EntityListeners(JpaEntityTraceListener.class)
@XmlRootElement
@Unique(members = { "token" })
@JsonIgnoreProperties(value = { "isAdmin" })
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
    private Set<String> roles = Sets.newLinkedHashSet();

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

    public Set<String> getRoles() {
        return Sets.newCopyOnWriteArraySet(roles);
    }

    public void setRoles(Set<String> role) {
        this.roles = Sets.newLinkedHashSet(role);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", idEmail)
                .add("name", name)
                .add("token", token)
                .add("version", version)
                .add("roles", roles)
                .toString();
    }
}
