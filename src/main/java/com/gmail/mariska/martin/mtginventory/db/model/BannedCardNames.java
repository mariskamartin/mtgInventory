package com.gmail.mariska.martin.mtginventory.db.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.gmail.mariska.martin.mtginventory.db.JpaEntityTraceListener;
import com.google.common.base.Objects;

/**
 * Slouzi jako seznam zabanovanych jmen karet, ktere se nemaji nacitat ani ukladat do db
 * @author MAR
 *
 */
@XmlRootElement
@Entity
@EntityListeners(JpaEntityTraceListener.class)
public class BannedCardNames {
    @Id
    private String idBannedName;

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id - banned card name", idBannedName)
                .toString();
    }

    public String getIdBannedName() {
        return idBannedName;
    }

    public void setIdBannedName(String idBannedName) {
        this.idBannedName = idBannedName;
    }
}
