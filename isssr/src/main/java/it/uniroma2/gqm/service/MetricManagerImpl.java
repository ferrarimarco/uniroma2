package it.uniroma2.gqm.service;

import it.uniroma2.gqm.dao.MetricDao;
import it.uniroma2.gqm.dao.QuestionMetricDao;
import it.uniroma2.gqm.model.GoalStatus;
import it.uniroma2.gqm.model.Measurement;
import it.uniroma2.gqm.model.Metric;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Question;
import it.uniroma2.gqm.model.QuestionMetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebService;

import org.appfuse.model.User;
import org.appfuse.service.impl.GenericManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("metricManager")
@WebService(serviceName = "MetricService", endpointInterface = "it.uniroma2.gqm.service.MetricManager")
public class MetricManagerImpl extends GenericManagerImpl<Metric, Long> implements MetricManager {
	
	private MetricDao metricDao;
	private QuestionMetricDao questionMetricDao;

	@Autowired
	public MetricManagerImpl(MetricDao metricDao, QuestionMetricDao questionMetricDao) {
		super(metricDao);
		this.metricDao = metricDao;
		this.questionMetricDao = questionMetricDao;
	}

	@Override
	public List<Metric> findByProject(Project project) {
		if(project !=null)
			return metricDao.findByProject(project.getId());
		else
			return null;
	}

	@Override
	public QuestionMetric getQuestionMetric(Metric metric, Question question) {
		return questionMetricDao.getQuestionMetric(question.getId(), metric.getId());
	}

	@Override
	public List<String> getAvailableStatus(QuestionMetric questionMetric, User user) {
		List<String> ret = new ArrayList<String>();
		switch (questionMetric.getStatus()) {
		case PROPOSED:
			ret.add(GoalStatus.PROPOSED.toString());				
			if(questionMetric.getQuestion().getQuestionOwner().equals(user)){			// only Question Stakeholder owner can approve a metric...
				ret.add(GoalStatus.FOR_REVIEW.toString());
				ret.add(GoalStatus.APPROVED.toString());
				ret.add(GoalStatus.REJECTED.toString());
			}
			break;
		case FOR_REVIEW:
			ret.add(GoalStatus.FOR_REVIEW.toString());				
			if(questionMetric.getMetric().getMetricOwner().equals(user)){			// only MMDM owner can propose a metric...
				ret.add(GoalStatus.PROPOSED.toString());
			}
			break;
		case APPROVED:
			ret.add(GoalStatus.APPROVED.toString());				
			break;
		case REJECTED:
			ret.add(GoalStatus.APPROVED.toString());				
			break;				

		}
		return ret;

	}

	@Override
	public QuestionMetric saveQuestionMetric(QuestionMetric questionMetric) {
		return questionMetricDao.save(questionMetric);
	}

	@Override
	public Metric findById(Long id){
		return get(id);
	}

	@Override
	public List<Double> getMeasuredMetricValues(Long metricId){
		Metric metric = get(metricId);
		List<Double> ret = new ArrayList<Double>();
		Iterator<Measurement> it = metric.getMeasurements().iterator();
		if(it.hasNext()){
			while (it.hasNext()) {
				Measurement mm = it.next();
				ret.add(mm.getValue());
			}
		}
		return ret;
	}

	@Override
	public List<String> getMetricInfo(Long metricId){
		Metric metric = get(metricId);
		List<String> ret = new ArrayList<String>();
		ret.add("Id: " + (metric.getId()) );
		ret.add("Name: " + (metric.getName()));
		if(metric.getUnit() != null)
			ret.add("Unit: " + (metric.getUnit().getName()));
		ret.add("Type: " + (metric.getType()));
		if(metric.getScale() != null)
			ret.add("Scale: " + (metric.getScale().getName()));
		ret.add("Collecting Type: " + (metric.getCollectingType()));
		return ret;
	}

	@Override
	public List<Metric> findByKeywords(List<String> keywords) {
		return metricDao.findByKeywords(keywords);
	}

	@Override
	public List<String> buildKeywordListFromCsvString(String keywordsCsv) {
		
		List<String> keywords = new ArrayList<String>();
		
		String[] keywordsElements = keywordsCsv.split(Metric.keywordsSeparator);
		
		if(keywordsElements.length > 0) {
			keywords.addAll(Arrays.asList(keywordsElements));
		}
		
		return keywords;
	}
}
