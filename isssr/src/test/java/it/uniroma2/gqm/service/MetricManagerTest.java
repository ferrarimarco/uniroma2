package it.uniroma2.gqm.service;

import it.uniroma2.gqm.dao.MetricDao;
import it.uniroma2.gqm.model.Metric;

import java.util.ArrayList;
import java.util.List;

import org.appfuse.service.impl.BaseManagerMockTestCase;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//@RunWith(SpringJUnit4ClassRunner.class)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
//@ContextConfiguration(classes={MetricManagerImpl.class})
public class MetricManagerTest extends BaseManagerMockTestCase {

	private MetricManager metricManager;

	private MetricDao metricDao;

	@Before
	public void setUp() {
		metricDao = context.mock(MetricDao.class);
		metricManager = new MetricManagerImpl(metricDao, null);
	}

	@Test
	public void getMetricsByKewordsTest() {

		final List<String> keywords = new ArrayList<String>();
		keywords.add("testKeyword1");

		Metric m = new Metric();
		
		String keywordsLine = "";
		
		for(String s : keywords) {
			keywordsLine += s + ",";
		}
		
		if(!keywordsLine.isEmpty()) {
			// Remove last coma
			keywordsLine = keywordsLine.substring(0, keywordsLine.length() - 1);
		}
		
		m.setKeywords(keywordsLine);
		
		final List<Metric> metrics = new ArrayList<Metric>();
		metrics.add(m);

		// set expected behavior on dao
		context.checking(new Expectations() {{
			one(metricDao).findByKeywords(keywords);
			will(returnValue(metrics));
		}});

		List<Metric> result = metricManager.findByKeywords(keywords);
		Assert.assertSame(result, metrics);
	}

}
