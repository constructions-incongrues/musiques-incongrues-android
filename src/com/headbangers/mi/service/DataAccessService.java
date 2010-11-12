package com.headbangers.mi.service;

import com.headbangers.mi.model.DataPage;

public interface DataAccessService {

    /**
     * Retourne les N derniers liens. TODO ajouter le segment de données afin de
     * rendre le code plus générique
     * 
     * @param nb
     * @return
     */
    DataPage retrieveLastNLinks(int nb);

    /**
     * Retourne N liens sélectionnés au hasard. TODO ajouter le segment de données afin
     * de rendre le code plus générique
     * 
     * @param nb
     * @return
     */
    DataPage retrieveShuffledNLinks(int nb);
}
