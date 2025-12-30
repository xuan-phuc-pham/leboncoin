package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import services.Facade;

@Controller
@SessionAttributes("courant")
@RequestMapping("/responsable")
public class RespController {
    private Facade facade;

    @Autowired
    public RespController(Facade facade) {
        this.facade = facade;
    }

    @RequestMapping("login")
    public String index(){
        return "/responsable/login";
    }
}


