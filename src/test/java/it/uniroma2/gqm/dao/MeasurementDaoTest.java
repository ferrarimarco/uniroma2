package it.uniroma2.gqm.dao;

import static org.junit.Assert.assertTrue;
import it.uniroma2.gqm.model.Measurement;

import java.util.ArrayList;
import java.util.List;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeasurementDaoTest extends BaseDaoTestCase {
	
	@Autowired
	private MeasurementDao measurementDao;
	
	@Test
	public void getMetricTest() {
		
		List<String> keywords = new ArrayList<String>();
		keywords.add("testk21");
		keywords.add("testk22");
		
		List<Measurement> measurement = measurementDao.findMeasurementByKeywordAndDate(keywords, "1970-01-01", "2100-01-01");
		
		assertTrue(measurement.size() == 1);
	}
	
}
