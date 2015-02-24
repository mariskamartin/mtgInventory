package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Date;
import java.util.UUID;

import javax.jdo.annotations.Unique;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

@Entity
@EntityListeners(JpaEntityTraceListener.class)
@XmlRootElement
@Unique(members={"name","foil","rarity","edition"})
public class Card {
    /**
     * As one place for META names
     */
    public static enum PROPS {
        id, name, foil, version, created, updated, rarity, edition
    }

    public Card() {}

    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean foil;
    @Version
    private long version;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date created;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date updated;

    @Enumerated(EnumType.STRING)
    private CardRarity rarity;
    @Enumerated(EnumType.STRING)
    private CardEdition edition;

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = Long.parseLong(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public CardRarity getRarity() {
        return rarity;
    }

    public void setRarity(CardRarity rarity) {
        this.rarity = rarity;
    }

    public CardEdition getEdition() {
        return edition;
    }

    @Transient
    public String getEditionKey() {
        return edition.getKey();
    }

    public void setEdition(CardEdition edition) {
        this.edition = edition;
    }

    public boolean isFoil() {
        return foil;
    }

    public void setFoil(boolean foil) {
        this.foil = foil;
    }

    @PrePersist
    private void prePersist() {
        created = new Date();
        updated = new Date();
    }

    @PreUpdate
    private void preUpdate() {
        updated = new Date();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", getName())
                .add("r", getRarity())
                .add("e", getEdition())
                .add("f", isFoil())
                .toString();
    }
}
