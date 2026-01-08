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


        Contact contact = facade.registerContact(
                "adumbledore",
             "fizwizbiz",
             "Albus",
             "Dumbledore",
             "Hogwarts",
             "The scottish wizarding school"
        );

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
            String login = "hpotter";
            String password = "expelliarmus";
            String lastName = "Potter";
            String firstName = "Harry";
            Organization organization = contact.getC_organization();

            boolean created = facade.registerMember(login, password, lastName, firstName,organization );

            if(created) {
                report.add(new TestResult("Register Non Existing Member", "OK", "Correctly registered the member."));
            } else {
                report.add(new TestResult("Register Non Existing Member", "KO", "Error: System didn't register the member."));
            }
        } catch (Exception e) {
            report.add(new TestResult("Register Non Existing Member", "KO", e.getMessage()));
        }

        try {
            String login = "tholland";
            String password = "expelliarmus";
            String lastName = "Potter";
            String firstName = "Harry";
            Organization organization = contact.getC_organization();
            boolean created = facade.registerMember(login, password, lastName, firstName,organization );

            if(!created) {
                report.add(new TestResult("Register Existing Member", "OK", "Correctly rejected: Login already exists."));
            } else {
                report.add(new TestResult("Register Existing Member", "KO", "Error: System allowed duplicate login!"));
            }
        } catch (Exception e) {
            report.add(new TestResult("Register Existing Member", "KO", e.getMessage()));
        }

        try {
            //Problem because it needs to be linked to an organization
            String login = "eleven";
            String password = "demorgogon";
            String firstName = "Jane";
            String lastName = "Hopper";
            String organizationName = "Stranger Things";
            String organizationDescription = "A small town in Indiana";

            Contact myContact = facade.registerContact(login, password, firstName, lastName, organizationName, organizationDescription);

            if(myContact != null) {
                report.add(new TestResult("Register Non Existing Contact", "OK", "Correctly registered the member."));
            } else {
                report.add(new TestResult("Register Non Existing Contact", "KO", "Error: System didn't register the member."));
            }
        } catch (Exception e) {
            report.add(new TestResult("Register Non Existing Contact", "KO", e.getMessage()));
        }

        try {
            String login = "tstark";
            String password = "demorgogon";
            String firstName = "Jane";
            String lastName = "Hopper";
            String organizationName = "Stranger Things";
            String organizationDescription = "A small town in Indiana";

            Contact myContact = facade.registerContact(login, password, firstName, lastName, organizationName, organizationDescription);

            if(myContact == null) {
                report.add(new TestResult("Register Existing Contact", "OK", "Correctly rejected: Login already exists."));
            } else {
                report.add(new TestResult("Register Existing Contact", "KO", "Error: System allowed duplicate login!"));
            }
        } catch (Exception e) {
            report.add(new TestResult("Register Existing Contact", "KO", e.getMessage()));
        }




        try {
            var offers = facade.getOffersByCategory(null);
            report.add(new TestResult("Get All Offers", "OK", "Found " + offers.size() + " offers."));
        } catch (Exception e) {
            report.add(new TestResult("Get All Offers", "KO", e.getMessage()));
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
            report.add(new TestResult("Member Wishes", "OK", "Member " + testId + "has " + wishes.size() + " wishes."));
        } catch (Exception e) {
            report.add(new TestResult("Member Wishes", "KO", "Error retrieving wishes: " + e.getMessage()));
        }

        model.addAttribute("testReport", report);
        return "tests";
    }


}
