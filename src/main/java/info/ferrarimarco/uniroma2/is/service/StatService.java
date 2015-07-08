package info.ferrarimarco.uniroma2.is.service;

public interface StatService {
    Double perishability(String entityId);
    Double success(String entityId);
    Double liking(String entityId);
    Double defecting(String entityId);
    void addRequested(String productId, Long value);
    void addDispensed(String productId, Long value);
    void addExpired(String productId, Long value);
    void addStocked(String productId, Long value);
    void addDefected(String productId, Long value);
    Long getRequested(String productId);
    Long getDispensed(String productId);
    Long getExpired(String productId);
    Long getStocked(String productId);
    Long getDefected(String productId);
    void initProductStat(String productId, String clazzId, String categoryId);
    void deleteByEntityId(String id);
}
