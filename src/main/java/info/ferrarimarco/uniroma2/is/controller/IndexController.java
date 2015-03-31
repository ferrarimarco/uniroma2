package info.ferrarimarco.uniroma2.is.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping(value = {"/index.html", "/"})
    public String index() {
        return "index.html";
    }
}
