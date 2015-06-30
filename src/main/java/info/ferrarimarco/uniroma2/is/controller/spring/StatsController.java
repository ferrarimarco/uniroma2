package info.ferrarimarco.uniroma2.is.controller.spring;

import info.ferrarimarco.uniroma2.is.controller.application.StatsApplicationController;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.dto.StatsDto;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/stats")
public class StatsController extends AbstractController {

    @Autowired
    private StatsApplicationController statsApplicationController;
    
    @ModelAttribute("allProducts")
    private List<Product> getAllProducts(){
        return loadEntityApplicationController.loadAllProducts();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, @ModelAttribute StatsDto statsDto){
        if(statsDto == null)
            statsDto = new StatsDto();

        if(!model.containsAttribute(Conventions.getVariableName(StatsDto.class)))
            model.addAttribute(statsDto);

        model.addAttribute(Constants.SUCCESS_INDEX, Constants.SUCCESS_INDEX);
        model.addAttribute(Constants.LIKING_INDEX, Constants.LIKING_INDEX);
        model.addAttribute(Constants.PERISHABILITY_INDEX, Constants.PERISHABILITY_INDEX);

        return "stats.html";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String getStats(Model model, @ModelAttribute StatsDto statsDto){
        if(!statsDto.isEmpty()){
            String criteriaId = null;
            if(!StringUtils.isBlank(statsDto.getCategoryId())){
                criteriaId = statsDto.getCategoryId();
                statsDto.setEntityName(statsDto.getCategoryName());
            }else if(!StringUtils.isBlank(statsDto.getClazzId())){
                criteriaId = statsDto.getClazzId();
                statsDto.setEntityName(statsDto.getClazzName());
            }else if(!StringUtils.isBlank(statsDto.getProductId())){
                criteriaId = statsDto.getProductId();
                statsDto.setEntityName(statsDto.getProductName());
            }else
                throw new IllegalArgumentException("Cannot choose a stat for: " + statsDto.toString());
            
            Double result = statsApplicationController.computeIndex(statsDto.getIndexType(), criteriaId);
            statsDto.setValue(result);
        }

        return index(model, statsDto);
    }
}
