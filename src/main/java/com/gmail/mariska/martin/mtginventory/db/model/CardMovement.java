package com.gmail.mariska.martin.mtginventory.db.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.jdo.annotations.Unique;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@EntityListeners(JpaEntityTraceListener.class)
@XmlRootElement
@Table(
    uniqueConstraints=@UniqueConstraint(name="uk_card_day_type_shop",columnNames={"card_id","day","type","shop"})
)
@Unique(members={"card","day","type","shop"})
public class CardMovement {
    /**
     * As one place for META names
     */
    public static enum PROPS {
        id, created, type, day, gainPrice, gainPercentage, newPrice, oldPrice, shop, card
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
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
    @JoinColumn(insertable=true, updatable=true, nullable=false)
    private Card card;

    public CardMovement(){
        super();
        this.created = new Date();
    }

    public CardMovement(Card card, CardMovementType type, CardShop shop, Date day, BigDecimal oldPrice, BigDecimal newPrice) {
        this();
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
        return id.toString();
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
