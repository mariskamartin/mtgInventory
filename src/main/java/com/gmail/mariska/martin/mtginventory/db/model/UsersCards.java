package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Date;
import java.util.UUID;

import javax.jdo.annotations.Unique;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;

@Entity
@Unique(members = { "userId", "cardId" })
@EntityListeners(JpaEntityTraceListener.class)
public class UsersCards {

    @Id
    private String id;
    private String userId;
    private String cardId;

    private long count;
    private boolean watched;
    private Date created;
    private Date updated;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean isWatched() {
        return watched;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    @PrePersist
    private void prePersist() {
        if (id == null || id.isEmpty() || id.equals("0")) {
            this.id = UUID.randomUUID().toString();
        }
        created = new Date();
    }

    @PreUpdate
    private void preUpdate() {
        updated = new Date();
    }
}
