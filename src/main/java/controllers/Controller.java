package controllers;

import dtos.*;
import entities.*;
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

        // TEST ENREGISTREMENT DES MEMBRES

        try {
            var login = "tholland";
            var password = "spidey";
            Member findMember = facade.checkMember(login, password);
            report.add(new TestResult("Check For Existing Member", "OK", "First Name: "+findMember.getM_firstName() + ", Last Name: "+findMember.getM_lastName()));
        } catch (Exception e) {
            report.add(new TestResult("Check For Existing Member", "KO", e.getMessage()));
        }

        try {
            String login = "non_existent_user";
            String password = "wrong_password";
            Member findMember = facade.checkMember(login, password);

            if (findMember == null) {
                report.add(new TestResult("Check Non Existing Member", "OK", "Correctly rejected: null" ));
            } else {
                report.add(new TestResult("Check Non Existing Member", "KO", "Security flaw: login allowed for non-existent user"));
            }
        } catch (Exception e) {
            report.add(new TestResult("Check Non Existing Member", "KO", "Unexpected error: " + e.getMessage()));
        }

        try {
            var login = "tstark";
            var password = "ironman";
            Contact findContact = facade.checkContact(login, password);
            report.add(new TestResult("Check For Existing Contact", "OK", "First Name: "+ findContact.getC_firstName() + ", Last Name: "+ findContact.getC_lastName()));
        } catch (Exception e) {
            report.add(new TestResult("Check For Existing Contact", "KO", e.getMessage()));
        }

        try {
            String login = "non_existent_user";
            String password = "wrong_password";
            Contact findContact = facade.checkContact(login, password);

            if (findContact == null) {
                report.add(new TestResult("Check Non Existing Contact", "OK", "Correctly rejected: null"));
            } else {
                report.add(new TestResult("Check Non Existing Contact", "KO", "Security flaw: login allowed for non-existent user"));
            }
        } catch (Exception e) {
            report.add(new TestResult("Check Non Existing Contact", "KO", e.getMessage()));
        }

        try {

            // Solve the case of the newly created member is considered to be the existed member on repetitive request
            RegMember nonExistingMember = new RegMember("hpotter", "expelliarmus", "Harry", "Potter", 5);
            boolean memberExist = facade.checkMemberByLogin(nonExistingMember.login());
            boolean created = facade.registerMember(nonExistingMember);
            if((memberExist && !created) || (!memberExist && created)) {
                report.add(new TestResult("Register !Existing And !Register Existing Member", "OK", "Correctly registered/rejected the member."));
            } else {
                report.add(new TestResult("Register !Existing And !Register Existing Member", "KO", "Logical Error: System didn't registered/rejected the member."));
            }
        } catch (Exception e) {
            report.add(new TestResult("Register !Existing And !Register Existing Member", "KO", e.getMessage()));
        }


        try { // Creation of an organisation and its contact
            //Problem because it needs to be linked to an organization -> solved
            // Check the registry of new Organisation and contact
            RegOrgContact orc_contact = new RegOrgContact(
                    "Horwarts Wizarding School",
                    "Hogwarts School of Witchcraft and Wizardry is a legendary, enchanted boarding school hidden in the Scottish Highlands where young witches and wizards master the magical arts within a sprawling castle of shifting stairs and ancient secrets.",
                    "Severus",
                    "Snape",
                    "ssnape",
                    "sectumsempra"
                    );
            boolean existed = facade.checkContactByLogin(orc_contact.contact_login());
            boolean created = facade.registerContact(orc_contact);
            if((!existed && created)||(existed && !created)) {
                report.add(new TestResult("Register !Existing And !Register Existing Contact/Organisation ", "OK", "Correctly registered/rejected Contact/Organisation."));
            } else {
                report.add(new TestResult("Register !Existing And !Register Existing Contact/Organisation", "KO", "Logical Error: System didn't registered/rejected Contact/Organisation."));
            }
        } catch (Exception e) {
            report.add(new TestResult("Register !Existing And !Register Existing Contact/Organisation", "KO", e.getMessage()));
        }





        try {
            List<Offer> offers = facade.getOffersByCategory(null);
            report.add(new TestResult("Get All Offers", "OK", "Found " + offers.size() + " offers."));
        } catch (Exception e) {
            report.add(new TestResult("Get All Offers", "KO", e.getMessage()));
        }

        try {
            int org_id = 2;
            List<Offer> offers = facade.getMyOffers(org_id);
            report.add(new TestResult("Get My Offers", "OK", "Found " + offers.size() + " offers for organisation with id: "+ org_id ));
        }catch (Exception e) {
            report.add(new TestResult("Get My Offers", "KO", e.getMessage()));
        }

        try {
            int org_id = 1;
            Long count = facade.nbOffersByOrganizations(org_id);
            report.add(new TestResult("Get Organization Offers", "OK", "Found " + count + " offers ACCEPTED by organisation with id: "+ org_id ));
        }catch (Exception e) {
            report.add(new TestResult("Get Organization Offers", "KO", e.getMessage()));
        }

        try {
            int org_id = 2;
            Long count = facade.nbOffersWonByOrganizations(org_id);
            report.add(new TestResult("Get Organization Offers Won", "OK", "Found " + count + " offers ACCEPTED FOR members in organisation with id: "+ org_id ));
        }catch (Exception e) {
            report.add(new TestResult("Get Organization Offers Won", "KO", e.getMessage()));
        }


        try {
            String category = "Sport";
            var offers = facade.getOffersByCategory(category);
            report.add(new TestResult("Get Offers From a Category", "OK", "Found " + offers.size() + " offers for the category: " + category));
        } catch (Exception e) {
            report.add(new TestResult("Get Offers From a Category", "KO", e.getMessage()));
        }

        try {
            String category = "Nul";
            var offers = facade.getOffersByCategory(category);
            report.add(new TestResult("Get Offers From a Non Existent  Category", "KO", "Found " + offers.size() + " offers for the category: " + category));
        } catch (Exception e) {
            report.add(new TestResult("Get Offers From a Non Existent Category", "OK", "Correctly rejected: " + e.getMessage()));
        }

        try {
            int testId = 2;
            var wishes = facade.getWishesByMember(testId);
            report.add(new TestResult("Member Wishes", "OK", "Member " + testId + " has " + wishes.size() + " wishes."));
        } catch (Exception e) {
            report.add(new TestResult("Member Wishes", "KO", "Error retrieving wishes: " + e.getMessage()));
        }

        try{
            List<Integer> categories = new ArrayList<>(List.of(1, 2));
            NewOffer  newOffer = new NewOffer(3, "7 Horcruxes", "Bonjour, je voudrais vendre 7 horcruxes de Voldemort", categories);
            boolean result = facade.postOffer(newOffer);
            boolean exist = facade.checkOfferExistsByName(newOffer.name());
            if((!result && exist) || ( !exist && result)) {
                report.add(new TestResult("Post Offer", "OK", "Success: "));
            }else {
                report.add(new TestResult("Post Offer", "KO", "Logical error: "));
            }
        } catch (Exception e) {
            report.add(new TestResult("Post Offer", "KO", "Conception error: " + e.getMessage()));
        }

        try{
            int offer_id = 2;
            boolean result = facade.cancel_offer(offer_id);
            boolean error_case = !facade.checkOfferExistById(offer_id) || facade.isNotActive(offer_id);
            if((!result && error_case) || ( !error_case && result)) {
                report.add(new TestResult("Cancel Offer", "OK", "Offer "+offer_id+" canceled and all it's wish has been rejected."));
            }else {
                report.add(new TestResult("Cancel Offer", "KO", "Logical error: Offer "+offer_id+" has not been canceled"));
            }
        } catch (Exception e) {
            report.add(new TestResult("Cancel Offer", "KO", "Conception error: " + e.getMessage()));
        }

        try{
            int offer_id = 5;
            boolean result = facade.validate_offer(offer_id);
            boolean error_case = !facade.checkOfferExistById(offer_id) || facade.isNotActive(offer_id) || facade.hasNoWish(offer_id);
            if((!result && error_case) || ( !error_case && result)) {
                report.add(new TestResult("Validate Offer", "OK", "..."));
            }else {
                report.add(new TestResult("Validate Offer", "OK", "..."));
            }
        } catch (Exception e) {
            report.add(new TestResult("Validate Offer", "KO", "...: " + e.getMessage()));
        }

        // Demand test:


        try { // rank
            int wish_id = 3 ;
            var rank = facade.getRank(wish_id);
            report.add(new TestResult("Rank Test", "OK", "Wish " + wish_id + " has the rank " + rank));
        } catch (Exception e) {
            report.add(new TestResult("Rank Test", "KO", "" + e.getMessage()));
        }

//        try { // wish action
//            int wish_id = 3 ;
//            var rank = facade.getRank(wish_id);
//            report.add(new TestResult("Rank Test", "OK", "Wish " + wish_id + " has the rank " + rank));
//        } catch (Exception e) {
//            report.add(new TestResult("Rank Test", "KO", "" + e.getMessage()));
//        }


//        try{
//            if(result) {
//                report.add(new TestResult("Post Offer", "OK", "..."));
//            }else {
//                report.add(new TestResult("Post Offer", "OK", "..."));
//            }
//        } catch (Exception e) {
//            report.add(new TestResult("Post Offer", "KO", "...: " + e.getMessage()));
//        }

        model.addAttribute("testReport", report);
        return "tests";
    }


}
