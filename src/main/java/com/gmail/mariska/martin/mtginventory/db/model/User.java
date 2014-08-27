package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.xml.bind.annotation.XmlRootElement;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;

@Entity
@EntityListeners(JpaEntityTraceListener.class)
@XmlRootElement
public class User {

    @Id
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PrePersist
    private void prePersist() {
        if (id == null || id.isEmpty() || id.equals("0")) {
            this.id = UUID.randomUUID().toString();
        }
    }

}
