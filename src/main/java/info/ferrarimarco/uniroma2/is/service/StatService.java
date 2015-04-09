package info.ferrarimarco.uniroma2.is.service;

import info.ferrarimarco.uniroma2.is.model.Entity;

public interface StatService {
    Double perishability(String entityId, Class<? extends Entity> clazz);
    Double success(String entityId, Class<? extends Entity> clazz);
    Double liking(String entityId, Class<? extends Entity> clazz);
}
