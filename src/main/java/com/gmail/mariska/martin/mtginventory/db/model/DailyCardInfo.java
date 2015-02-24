package com.gmail.mariska.martin.mtginventory.db.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.jdo.annotations.Unique;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

@XmlRootElement
@Entity
@EntityListeners(JpaEntityTraceListener.class)
@Unique(members={"card","day","shop"})
public class DailyCardInfo {
    /**
     * As one place for META names
     */
    public static enum PROPS {
        id, price, storeAmount, shop, day, card
    }



    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;

    private long storeAmount;

    @Enumerated(EnumType.STRING)
    private CardShop shop;

    @Temporal(TemporalType.DATE)
    private Date day;

    @ManyToOne(cascade=CascadeType.MERGE, optional = false)
    @JoinColumn(name = "card_id",  insertable=true, updatable=true, nullable=false)
    private Card card;

    public DailyCardInfo() {}

    public DailyCardInfo(Card card, BigDecimal price, long storeAmount, Date date, CardShop shop) {
        this();
        this.shop = shop;
        this.card = card;
        this.price = price;
        this.storeAmount = storeAmount;
        this.setDay(date);
    }

    @XmlTransient
    @JsonIgnore
    public Card getCard() {
        return card;
    }

    @XmlTransient
    @JsonIgnore
    public String getId() {
        return id.toString();
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public long getStoreAmount() {
        return storeAmount;
    }

    public void setStoreAmount(long storeAmount) {
        this.storeAmount = storeAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDay() {
        return day;
    }

    public String getDayTxt() {
        return new SimpleDateFormat("yyyy-MM-dd").format(day);
    }

    public void setDay(Date day) {
        this.day = new Date(day.getTime());
    }

    public CardShop getShop() {
        return shop;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
//                .add("card", card != null ? card.toString() : "null")
//                .add("cardId", card != null ? card.getId() : "null")
                .add("shop", getShop())
                .add("cena", price)
                .add("skladem", storeAmount)
                .add("day", day)
                .toString();
    }


    @PrePersist
    private void prePersist() {
    }
}
