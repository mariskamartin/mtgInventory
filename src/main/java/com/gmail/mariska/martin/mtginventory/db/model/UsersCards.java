package com.gmail.mariska.martin.mtginventory.db.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;

@Entity
@EntityListeners(JpaEntityTraceListener.class)
public class UsersCards {
    @OneToOne
    @MapsId
    private User user;

    @OneToOne
    @MapsId
    private Card card;

    private long count;

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
}
