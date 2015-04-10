package info.ferrarimarco.uniroma2.is.controller;

import java.util.List;

import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.model.dto.StatsDto;

import org.springframework.core.Conventions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/stats")
public class StatsController extends AbstractController {
    
    @ModelAttribute("allProducts")
    private List<Product> getAllProducts(){
        return productPersistenceService.findAll();
    }
    
    @RequestMapping
    public String index(Model model){
        if(!model.containsAttribute(Conventions.getVariableName(StatsDto.class)))
            model.addAttribute(new StatsDto());
        return "stats.html";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String postStatRequest(Model model, @ModelAttribute StatsDto statsDto){
        return "stats.html";
    }
}
