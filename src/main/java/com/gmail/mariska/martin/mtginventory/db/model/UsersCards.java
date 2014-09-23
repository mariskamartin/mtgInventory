package com.gmail.mariska.martin.mtginventory.db.model;

import javax.jdo.annotations.Unique;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;

@Entity
@Unique(members = { "user", "card" })
@EntityListeners(JpaEntityTraceListener.class)
public class UsersCards {
    @ManyToOne
    @JoinColumn(name = "idEmail")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id")
    private Card card;

    private long count;
    private boolean watched;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
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
}
