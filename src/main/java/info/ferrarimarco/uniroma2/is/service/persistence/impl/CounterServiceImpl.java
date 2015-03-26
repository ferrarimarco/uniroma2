package info.ferrarimarco.uniroma2.is.service.persistence.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Counter;
import info.ferrarimarco.uniroma2.is.service.persistence.CounterService;

@Service
public class CounterServiceImpl implements CounterService {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public long getNextCategorySequence() {
        return increaseCounter(Constants.CATEGORY_SYM_ID_COUNTER_NAME);
    }

    @Override
    public long getNextClazzSequence() {
        return increaseCounter(Constants.CLASS_SYM_ID_COUNTER_NAME);
    }

    @Override
    public long getNextProductSequence() {
        return increaseCounter(Constants.PRODUCT_SYM_ID_COUNTER_NAME);
    }

    private long increaseCounter(String counterName){
        Query query = new Query(Criteria.where("name").is(counterName));
        Update update = new Update().inc("sequence", 1);
        Counter counter = mongoTemplate.findAndModify(query, update, Counter.class); // return old Counter object
        // TODO: check if collection exists
        return counter.getSequence();
    }
}