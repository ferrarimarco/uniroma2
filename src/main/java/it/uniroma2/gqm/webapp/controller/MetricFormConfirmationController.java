package it.uniroma2.gqm.webapp.controller;

import it.uniroma2.gqm.model.Metric;
import it.uniroma2.gqm.service.MetricManager;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/metricformconfirmation*")
@SessionAttributes({"currentMetric"})
public class MetricFormConfirmationController extends BaseFormController{
	
    @Autowired
    private MetricManager metricManager;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView handleRequest(HttpSession session) throws Exception {
        Metric currentMetric = (Metric) session.getAttribute("currentMetric");
        
        List<Metric> metricsWithSameKeywords = metricManager.findByKeywords(currentMetric.getKeywordList());
        
        // remove the newly created metric
        for(Metric m : metricsWithSameKeywords) {
        	if(m.equals(currentMetric)) {
        		metricsWithSameKeywords.remove(m);
        		break;
        	}
        }
        
		return new ModelAndView().addObject(metricsWithSameKeywords);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

		if (request.getParameter("cancel") != null) {
			return getCancelView();
		}
		
		log.debug("entering 'onSubmit' method...");
		
		Locale locale = request.getLocale();
		if (request.getParameter("delete") != null) {
			
			Metric currentMetric = (Metric) session.getAttribute("currentMetric");
			
			metricManager.remove(currentMetric.getId());
			saveMessage(request, getText("metric.deleted", locale));
		}else {
			saveMessage(request, getText("metric.added", locale));
		}
		
		session.removeAttribute("currentMetric");
		
		return "redirect:metrics";
	}
}
