package info.ferrarimarco.uniroma2.is.controller;

import info.ferrarimarco.uniroma2.is.model.dto.StatsDto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stats")
public class StatsController extends AbstractController {
    
    @RequestMapping
    public String index(Model model){
        model.addAttribute(new StatsDto());
        return "stats.html";
    }
}
