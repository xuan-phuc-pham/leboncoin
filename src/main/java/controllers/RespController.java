package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import services.MemFacade;

@Controller
@SessionAttributes("courant")
@RequestMapping("/responsable")
public class RespController {
//    private MemFacade facade;
//
//    @Autowired
//    public RespController(MemFacade facade) {
//        this.facade = facade;
//    }

    @RequestMapping("login")
    public String index(){
        return "/responsable/login";
    }
}


