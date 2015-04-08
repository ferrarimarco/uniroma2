package info.ferrarimarco.uniroma2.is.service;

import info.ferrarimarco.uniroma2.is.model.Entity;

public interface StatService {
    Double deperibilita();
    Double successo(String entityId, Class<? extends Entity> clazz);
    Double gradimento();
    Double gradimentoMedioGiornaliero();
}
