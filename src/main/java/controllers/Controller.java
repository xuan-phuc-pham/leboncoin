package controllers;

import dtos.*;
import entities.Wish;
import entities.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import services.Facade;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {

    private final Facade facade;

    @Autowired
    public Controller(Facade facade) {
        this.facade = facade;
    }

    public record TestResult(String testName, String testStatus, String testDetails) {}

    @RequestMapping("")
    public String displayTests(Model model) {
        List<TestResult> report = new ArrayList<>();

        // Test 1: Offers
        try {
            var offers = facade.getOffers();
            report.add(new TestResult("Public Offers", "OK", "Found " + offers.size() + " offers."));
        } catch (Exception e) {
            report.add(new TestResult("Public Offers", "KO", e.getMessage()));
        }

        // Test 2: Member Wishes
        try {
            int testId = 1;
            var wishes = facade.getWishesByMember(testId);
            report.add(new TestResult("Member Wishes", "OK", "Member ID 1 has " + wishes.size() + " wishes."));
        } catch (Exception e) {
            report.add(new TestResult("Member Wishes", "KO", "Error retrieving wishes: " + e.getMessage()));
        }

        model.addAttribute("testReport", report);
        return "tests";
    }

//
//    @RequestMapping("login")
//    public String login(Model model, MemberCredential mem_cred){
//        if (model.getAttribute("courant") != null){
//            return "redirect:dashboard";
//        }else{
//            if (facade.checkLP(mem_cred.login(), mem_cred.password())) {
//                model.addAttribute("courant", facade.retrieveMemberId(mem_cred.login()));
//                return "redirect:dashboard";
//            } else{
//                if( mem_cred.login() != null) {
//                    model.addAttribute("error", "Authentication Failed");
//                }
//                return "member/login";
//            }
//        }
//    }
//
//    @RequestMapping("dashboard")
//    public String dashboard(Model model){
//        if (model.getAttribute("courant") == null){
//            return "member/login";
//        } else{
//            Integer courant = (Integer) model.getAttribute("courant");
//            MemberInfo mi = facade.getMemberInfo(courant);
//            model.addAttribute("member", mi);
//            return "member/dashboard";
//        }
//    }
//
//    @RequestMapping("logout")
//    public String logout(SessionStatus session, Model model){
//        session.setComplete();
//        model.addAttribute("member", null);
//        return "redirect:/";
//    }
//
//    @RequestMapping("offers")
//    public String offers(Model model){
//        if (model.getAttribute("courant") == null){
//            return "member/login";
//        } else {
//            MemberInfo mi = facade.getMemberInfo((Integer) model.getAttribute("courant"));
//            model.addAttribute("member", mi);
//            List<Offer> offers = facade.getPublicFacade().getOffers();
//            List<OfferInfo> list_offers = facade.getPublicFacade().getOffersInfo(offers);
//            model.addAttribute("offers", list_offers);
//            return "public/offers";
//        }
//    }
//    @GetMapping("/offer/detail/{id}")
//    public String offerDetail(Model model, @PathVariable("id") int id){
//        if (model.getAttribute("courant") == null){
//            return "member/login";
//        } else {
//            MemberInfo mi = facade.getMemberInfo((Integer) model.getAttribute("courant"));
//            model.addAttribute("member", mi);
//            OfferInfo of_detail = facade.getPublicFacade().getOfferById(id);
//            model.addAttribute("of_detail", of_detail);
//            int courant = (int) model.getAttribute("courant");
//            model.addAttribute(
//                    "can_apply",
//                    !facade.alreadySubmitted(courant, id) && !facade.isMemberInOrganisation(courant, id)
//            );
//            List<Wish> ld = facade.getWishesByOffer(id);
//            List<WishInfo> list_Wishes = facade.getDemandsInfo(ld);
//            model.addAttribute("Wishes", list_Wishes);
//            return "member/offer_detail";
//        }
//    }
//
//    @RequestMapping("/demand")
//    public String demand(Model model, WishSubmit ds){
//        if (model.getAttribute("courant") == null){
//            return "member/login";
//        } else{
//            int of_id = ds.of_id();
//            int mem_id = (Integer)model.getAttribute("courant");
//            if( !facade.alreadySubmitted(mem_id, of_id) && !facade.isMemberInOrganisation(mem_id, of_id)){
//                facade.wish(mem_id, of_id);
//                return "redirect:offer/detail/"+of_id;
//            } else{
//                return "redirect:offer/detail/"+of_id;
//            }
//
////            return "redirect:/member/dashboard";
//        }

//    }






//    public String register(Model model, MemberCredentials mem_cred){
//
//    }



}
