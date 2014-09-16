package com.gmail.mariska.martin.mtginventory.sniffer;

import java.io.IOException;
import java.util.List;

import com.gmail.mariska.martin.mtginventory.db.model.CardEdition;
import com.gmail.mariska.martin.mtginventory.db.model.DailyCardInfo;

/**
 * Rozhrani pro stahovani karet z obchodu
 * @author MAR
 *
 */
public interface ISniffStrategy {
    /**
     * Stahne karty podle jmena
     * @param name
     * @return
     */
    public List<DailyCardInfo> sniffByCardName(String name) throws IOException;

    /**
     * Stahne karty dane edice
     * @param edition
     * @return
     */
    public List<DailyCardInfo> sniffByEdition(CardEdition edition) throws IOException;
}
