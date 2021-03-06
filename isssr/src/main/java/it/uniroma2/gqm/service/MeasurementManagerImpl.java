package it.uniroma2.gqm.service;

import java.util.List;

import it.uniroma2.gqm.dao.MeasurementDao;
import it.uniroma2.gqm.dao.MetricDao;
import it.uniroma2.gqm.dao.QuestionMetricDao;
import it.uniroma2.gqm.model.Measurement;
import it.uniroma2.gqm.model.Metric;

import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("measurementManager")
@WebService(serviceName = "measureService", endpointInterface = "it.uniroma2.gqm.service.MeasurementManager")
public class MeasurementManagerImpl  extends GenericManagerImpl<Measurement, Long> implements MeasurementManager {
	
	private MeasurementDao measurementDao;   
    
    @Autowired
    public MeasurementManagerImpl(MeasurementDao measurementDao) {
        super(measurementDao);
        this.measurementDao = measurementDao;
    }

    public List<Measurement> findMeasuremntsByMetric(Metric metric){
    	if(metric !=null)
    		return measurementDao.findMeasuremntsByMetric(metric);
    	else
    		return null;
    }

	@Override
	public List<Measurement> findMeasurementByKeywordsAndDate(
			List<String> keywords, String min, String max) {
		return measurementDao.findMeasurementByKeywordAndDate(keywords,min,max);
	}

    
}
