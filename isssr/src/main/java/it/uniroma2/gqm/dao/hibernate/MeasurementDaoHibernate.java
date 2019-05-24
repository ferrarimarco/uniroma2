package it.uniroma2.gqm.dao.hibernate;

import it.uniroma2.gqm.dao.MeasurementDao;
import it.uniroma2.gqm.dao.MetricDao;
import it.uniroma2.gqm.model.Measurement;
import it.uniroma2.gqm.model.Metric;

import java.util.Collections;
import java.util.List;

import org.appfuse.dao.hibernate.GenericDaoHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository("measurementDao")
public class MeasurementDaoHibernate extends GenericDaoHibernate<Measurement, Long>  implements MeasurementDao {
		
    public MeasurementDaoHibernate() {
        super(Measurement.class);
    }
    public List<Measurement> findMeasuremntsByMetric(Metric metric){
    	Query q =  getSession().getNamedQuery("findMeasuremntsByMetric").setLong("metric_id", metric.getId());
    	return q.list();
    }

	@Override
	public List<Measurement> findMeasurementByKeywordAndDate(
			List<String> keywords, String min, String max) {

		Collections.sort(keywords);
		
		String keywordCriteria = "%";
		
		for(String s : keywords) {
			keywordCriteria += s + "%";
		}
		Query q = getSession().getNamedQuery("findMeasurementByKeywordAndDate").
				setString("keywords", keywordCriteria).setString("min", min).setString("max", max);
		return q.list();
	}
}
