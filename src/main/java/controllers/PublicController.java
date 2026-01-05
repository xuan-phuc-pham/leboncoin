package controllers;

import dtos.*;
import entities.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import services.MemFacade;
import services.PublicFacade;

import java.util.List;


@Controller
@SessionAttributes("courant")
@RequestMapping("/public")
public class PublicController {
    private PublicFacade facade;

    @Autowired
    public PublicController(PublicFacade facade) {
        this.facade = facade;
    }

    @RequestMapping("")
    public String index() {
        return "redirect:/public/dashboard";
    }

    @RequestMapping("dashboard")
    public String dashboard(Model model){
        if (model.getAttribute("courant") != null){
            return "redirect:/member/dashboard";
        }
        return "/public/dashboard";
    }

    @RequestMapping("offers")
    public String offres(Model model){
        List<Offer> lo = facade.getOffers();
        List<OfferInfo> list_offers = facade.getOffersInfo(lo);
        model.addAttribute("offers", list_offers);
        return "/public/offers";
    }


}
