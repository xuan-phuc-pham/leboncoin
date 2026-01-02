package controllers;

import dtos.MemberCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import services.MemFacade;

@Controller
@SessionAttributes("courant")
@RequestMapping("/member")
public class MemController {
    private MemFacade facade;

    @Autowired
    public MemController(MemFacade facade) {
        this.facade = facade;
    }

    @RequestMapping("")
    public String index(Model model){
        if (model.getAttribute("courant") == null){
            return "member/login";
        } else{
            return "member/dashboard";
        }
    }

    @RequestMapping("login")
    public String login(Model model, MemberCredential mem_cred){
        if (facade.checkLP(mem_cred.login(), mem_cred.password())) {
            model.addAttribute("courant", mem_cred.login());
            model.addAttribute("username", mem_cred.login());
            return "member/dashboard";
        }
        return "";
    }


//    public String register(Model model, MemberCredentials mem_cred){
//
//    }



}
