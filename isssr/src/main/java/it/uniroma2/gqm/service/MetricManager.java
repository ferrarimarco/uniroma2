package it.uniroma2.gqm.service;

import it.uniroma2.gqm.model.Metric;
import it.uniroma2.gqm.model.Project;
import it.uniroma2.gqm.model.Question;
import it.uniroma2.gqm.model.QuestionMetric;

import java.util.List;

import javax.jws.WebService;

import org.appfuse.model.User;
import org.appfuse.service.GenericManager;

@WebService
public interface MetricManager extends GenericManager<Metric, Long> {
	
	Metric findById(Long id);
	List<Metric> findByProject(Project project);
	List<Metric> findByKeywords(List<String> keywords);
	
	QuestionMetric getQuestionMetric(Metric metric,Question question);
	QuestionMetric saveQuestionMetric(QuestionMetric questionMetric);
	
	List<Double> getMeasuredMetricValues(Long metricId);
	List<String> getMetricInfo(Long metricId);
	List<String> getAvailableStatus(QuestionMetric questionMetric, User user);
	
	List<String> buildKeywordListFromCsvString(String keywordsCsv);
}