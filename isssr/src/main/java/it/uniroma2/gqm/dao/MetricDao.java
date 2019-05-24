package it.uniroma2.gqm.dao;

import it.uniroma2.gqm.model.Metric;

import java.util.List;

import org.appfuse.dao.GenericDao;

public interface MetricDao extends GenericDao<Metric, Long> {
	public List<Metric> findByProject(Long id);
	public List<Metric> findByKeywords(List<String> keywords);
}
