package it.uniroma2.gqm.dao;

import it.uniroma2.gqm.model.GoalQuestion;
import it.uniroma2.gqm.model.Question;

import java.util.List;

import org.appfuse.dao.GenericDao;


public interface GoalQuestionDao extends GenericDao<GoalQuestion, Long> {
    public GoalQuestion getGoalQuestion(Long goalId,Long questionId); 
}