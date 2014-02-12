package it.uniroma2.gqm.service;

import java.util.ArrayList;
import java.util.List;

import org.appfuse.service.impl.BaseManagerMockTestCase;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.uniroma2.gqm.dao.MeasurementDao;
import it.uniroma2.gqm.model.Measurement;
import it.uniroma2.gqm.model.Metric;

public class MeasurementManagerTest extends BaseManagerMockTestCase {

	private MeasurementManager measurementManager;
	
	private MeasurementDao measurementDao;

	@Before
	public void setUp() {
		measurementDao = context.mock(MeasurementDao.class);
		measurementManager = new MeasurementManagerImpl(measurementDao);
	}
	
	@Test
	public void getMeasureByKewordsTest() {

		final List<String> keywords = new ArrayList<String>();
		keywords.add("testk11");

		Metric m = new Metric();
		
		String keywordsLine = "";
		
		for(String s : keywords) {
			keywordsLine += s + ",";
		}
		
		if(!keywordsLine.isEmpty()) {
			// Remove last comma
			keywordsLine = keywordsLine.substring(0, keywordsLine.length() - 1);
		}
		
		m.setKeywords(keywordsLine);
		Measurement mes = new Measurement();
		mes.setMetric(m);
		
		final List<Measurement> measurements = new ArrayList<Measurement>();
		measurements.add(mes);

		// set expected behavior on dao
		context.checking(new Expectations() {{
			one(measurementDao).findMeasurementByKeywordAndDate(keywords,"1970-01-01","2100-01-01");
			will(returnValue(measurements));
		}});

		List<Measurement> result = measurementManager.findMeasurementByKeywordsAndDate(keywords,"1970-01-01","2100-01-01");
		Assert.assertSame(result, measurements);
	}
}

