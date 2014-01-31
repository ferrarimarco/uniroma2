package it.uniroma2.gqm.dao;

import it.uniroma2.gqm.model.Metric;

import java.util.ArrayList;
import java.util.List;

import org.appfuse.dao.BaseDaoTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class MetricDaoTest extends BaseDaoTestCase {
	
	@Autowired
	private MetricDao metricDao;
	
	@Test
	public void getMetricTest() {
		
		List<String> keywords = new ArrayList<String>();
		keywords.add("testk11");
		keywords.add("testk12");
		
		List<Metric> metrics = metricDao.findByKeywords(keywords);
		
		assertTrue(metrics.size() == 1);
	}
	
}
