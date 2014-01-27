package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.Goal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component(value="goalValidator")
public class GoalValidator implements Validator {  
	  /** 
	   * Return true if this object can validate objects of the given class. This is cargo-cult 
	   * code: all implementations are the same and can be cut 'n' pasted from earlier examples. 
	   */  
	  @Override  
	  public boolean supports(Class<?> clazz) {  	  
	    return Goal.class.equals(clazz);
	  }  

	  /** 
	   * Validate an object, which must be a class type for which the supports() method returned 
	   * true. 
	   * 
	   * @param obj The target object to validate 
	   * @param errors contextual state info about the validation process (never null) 
	   */  

	@Override
	public void validate(Object arg0, Errors errors) {
		Goal g = (Goal) arg0;  
		if(!"GQM+Strategies".equalsIgnoreCase(g.getInterpretationModelAsString())){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject","subject", "Subject is a required field if an interpretation model different from GQM+Strategies is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "context","context", "Context is a required field if an interpretation model different from GQM+Strategies is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "viewpoint","viewpoint", "Viewpoint is a required field if an interpretation model different from GQM+Strategies is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "impactOfVariation","impactOfVariation", "Impact of variation is a required field if an interpretation model different from GQM+Strategies is selected.");
		}else {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "activity","activity", "Activity is a required field if GQM+Strategies interpretation model is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "object","object", "Object is a required field if GQM+Strategies interpretation model is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "magnitude","magnitude", "Magnitude is a required field if GQM+Strategies interpretation model is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "timeframe","timeframe", "Timeframe is a required field if GQM+Strategies interpretation model is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "constraints","constraints", "Constraints is a required field if GQM+Strategies interpretation model is selected.");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "relations","relations", "Relations is a required field if GQM+Strategies interpretation model is selected.");
		}
	}  
}
