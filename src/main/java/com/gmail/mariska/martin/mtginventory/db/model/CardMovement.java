package com.gmail.mariska.martin.mtginventory.db.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.jdo.annotations.Unique;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

@Entity
@EntityListeners(JpaEntityTraceListener.class)
@XmlRootElement
@Unique(members={"card","day","type","shop"})
public class CardMovement {
    /**
     * As one place for META names
     */
    public static enum PROPS {
        id, created, type, day, gainPrice, gainPercentage, newPrice, oldPrice, shop, card
    }

    @Id
    private String id;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date created;
    @Enumerated(EnumType.STRING)
    private CardMovementType type;
    @Enumerated(EnumType.STRING)
    private CardShop shop;
    @Temporal(TemporalType.DATE)
    private Date day;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal gainPrice;
    private double gainPercentage;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id", nullable = false, updatable = false)
    private Card card;

    public CardMovement(Card card, CardMovementType type, CardShop shop, Date day, BigDecimal oldPrice, BigDecimal newPrice) {
        super();
        this.type = type;
        this.shop = shop;
        this.day = day;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.gainPrice = newPrice.subtract(oldPrice);
        this.gainPercentage = (newPrice.doubleValue() / oldPrice.doubleValue() * 100) - 100;
        this.card = card;
    }

    public String getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public CardMovementType getType() {
        return type;
    }

    public void setType(CardMovementType type) {
        this.type = type;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public CardShop getShop() {
        return shop;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public BigDecimal getGainPrice() {
        return gainPrice;
    }

    public void setGainPrice(BigDecimal gainPrice) {
        this.gainPrice = gainPrice;
    }

    public double getGainPercentage() {
        return Math.abs(gainPercentage) > 999 ? 999 : gainPercentage;
    }

    public void setGainPercentage(double gainPercentage) {
        this.gainPercentage = gainPercentage;
    }

    @PrePersist
    private void prePersist() {
        if (id == null || id.isEmpty() || id.equals("0")) {
            this.id = UUID.randomUUID().toString();
        }
        created = new Date();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("day", day)
                .add("type", type)
                .add("old", oldPrice)
                .add("new", newPrice)
                .add("gain", getGainPrice())
                .add("gain %", gainPercentage)
                .toString();
    }
}
