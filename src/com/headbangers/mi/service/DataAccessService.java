package com.headbangers.mi.service;

import com.headbangers.mi.model.DataPage;

public interface DataAccessService {

    /**
     * Retourne les N derniers liens.
     * 
     * @param nb
     * @return
     */
    DataPage retrieveLastNLinks(Segment segment, int nb);
    
    /**
     * Retourne un intervalle de lien compris entre 'offset' et 'max'.
     * Exemple: retrieveRangeLinks (Segment.MP3, 0, 10); retourne les 10 premiers
     * mp3s.    retrieveRangeLinks (Segment.MP3, 10, 10); retourne la seconde page.
     * @param segment
     * @param offset
     * @param max
     * @return
     */
    DataPage retrieveRangeLinks (Segment segment, int offset, int max);

    /**
     * Retourne N liens sélectionnés au hasard.
     * 
     * @param nb
     * @return
     */
    DataPage retrieveShuffledNLinks(Segment segment, int nb);
}
