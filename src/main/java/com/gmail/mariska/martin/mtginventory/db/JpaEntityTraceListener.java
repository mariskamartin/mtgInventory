package com.gmail.mariska.martin.mtginventory.db;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.log4j.Logger;

public class JpaEntityTraceListener {
    private static final Logger logger = Logger.getLogger(JpaEntityTraceListener.class);

    @PrePersist
    void onPrePersist(Object o) {
        log("prePersist: ", o);
    }

    @PostPersist
    void onPostPersist(Object o) { log("postPersist: ", o); }

    @PostLoad
    void onPostLoad(Object o) { log("postLoad: ", o); }

    @PreUpdate
    void onPreUpdate(Object o) {
        log("preUpdate: ", o);
    }

    @PostUpdate
    void onPostUpdate(Object o) { log("postUpdate: ", o); }

    @PreRemove
    void onPreRemove(Object o) {
        log("preRemove: ", o);
    }

    @PostRemove
    void onPostRemove(Object o) {
    }

    private void log(String prefix, Object o) {
        if (logger.isTraceEnabled()) {
            logger.trace(prefix + o);
        }
    }
}
