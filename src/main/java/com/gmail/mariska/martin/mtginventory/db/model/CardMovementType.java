package com.gmail.mariska.martin.mtginventory.db.model;

import java.util.Date;

import com.gmail.mariska.martin.mtginventory.utils.Utils;

public enum CardMovementType {
    /**
     * Vraci predchozi den
     */
    DAY {
        @Override
        public Date getDayFrom(Date day) {
            return Utils.dayAdd(day, -1);
        }
    },
    /**
     * Vraci den na zacatku tydne
     */
    START_OF_WEEK {
        @Override
        public Date getDayFrom(Date day) {
            return Utils.cutoffToMonday(day);
        }
    };

    public abstract Date getDayFrom(Date day);
}
