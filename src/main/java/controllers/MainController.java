package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import services.MemFacade;


@Controller
@SessionAttributes("courant")
@RequestMapping("/")
public class MainController {

//    private MemFacade facade;
//
//    @Autowired
//    public PublicController(MemFacade facade) {
//        this.facade = facade;
//    }

    @RequestMapping("")
    public String index(Model model) {
        if (model.getAttribute("courant") == null) {
            return "redirect:/public";
        } else {
            return "redirect:/member";
        }

    }


}
