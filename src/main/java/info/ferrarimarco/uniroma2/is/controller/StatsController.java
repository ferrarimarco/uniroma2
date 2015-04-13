package info.ferrarimarco.uniroma2.is.controller;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Constants;
import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.dto.StatsDto;
import info.ferrarimarco.uniroma2.is.service.StatService;

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
    private StatService statService;

    @ModelAttribute("allProducts")
    private List<Product> getAllProducts(){
        return productPersistenceService.findAll();
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
            Class<? extends Entity> criteriaClass = null;
            String criteriaId = null;
            if(!StringUtils.isBlank(statsDto.getCategoryId())){
                criteriaClass = Category.class;
                criteriaId = statsDto.getCategoryId();
                statsDto.setEntity(categoryPersistenceService.findById(criteriaId));
            }else if(!StringUtils.isBlank(statsDto.getClazzId())){
                criteriaClass = Clazz.class;
                criteriaId = statsDto.getClazzId();
                statsDto.setEntity(clazzPersistenceService.findById(criteriaId));
            }else if(!StringUtils.isBlank(statsDto.getProductId())){
                criteriaClass = Product.class;
                criteriaId = statsDto.getProductId();
                statsDto.setEntity(productPersistenceService.findById(criteriaId));
            }else{
                throw new IllegalArgumentException("Cannot choose a stat for: " + statsDto.toString());
            }
            try{
            switch(statsDto.getIndexType()){
            case Constants.SUCCESS_INDEX:
                statsDto.setValue((statService.success(criteriaId, criteriaClass)));
                break;
            case Constants.LIKING_INDEX:
                statsDto.setValue((statService.liking(criteriaId, criteriaClass)));
                break;
            case Constants.PERISHABILITY_INDEX:
                statsDto.setValue((statService.perishability(criteriaId, criteriaClass)));
                break;
            default:
                throw new IllegalArgumentException("Cannot choose a index for: " + statsDto.toString());
            }
            }catch(ArithmeticException e){
                statsDto.setValue(0.0);
            }
        }

        return index(model, statsDto);
    }
}
