package com.gmail.mariska.martin.mtginventory.db.validators;

import com.gmail.mariska.martin.mtginventory.MtgInventoryException;

public class BannedCardException extends MtgInventoryException {
    private static final long serialVersionUID = -9151699132437308103L;

    public BannedCardException() {
        super("Operation with Banned card");
    }
}
