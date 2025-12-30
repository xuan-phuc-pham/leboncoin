package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import services.Facade;

@Controller
@SessionAttributes("courant")
@RequestMapping("/member")
public class MemController {
    private Facade facade;

    @Autowired
    public MemController(Facade facade) {
        this.facade = facade;
    }

    @RequestMapping("")
    public String index(){
        return "dashboard";
    }

}
