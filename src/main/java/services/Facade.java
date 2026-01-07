package services;

import entities.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import type.Status;

import java.time.LocalDateTime;
import java.util.*;

import static type.WishStatus.DELIVERED;

@Service
public class Facade {
    @PersistenceContext
    private EntityManager em;


    public Facade() {
    }

    // CHECK AND REGISTER PEOPLE

    public Member checkMember(String login, String password) {
        // Search for a member in the Member table
        try {
            Query q = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class);
            q.setParameter("login", login);

            Member member = (Member) q.getSingleResult();

            if (member != null && member.getM_password().equals(password)) {
                return member;
            }
        } catch (NoResultException e) {
            System.out.println("No member found with login: " + login);
            return null;
        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            return null;
        }

        return null;
    }

    public Contact checkContact(String login, String password) {
        // Search a contact in the Contact Table
        try {
            Query q = em.createQuery("SELECT c FROM Contact c WHERE c.c_login = :login", Contact.class);
            q.setParameter("login", login);

            Contact contact = (Contact) q.getSingleResult();

            if (contact != null && contact.getC_password().equals(password)) {
                return contact;
            }
        } catch (NoResultException e) {
            System.out.println("No contact found with login: " + login);
            return null;
        }  catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
            return null;
        }

        return null;
    }



    @Transactional
    public boolean registerMember(Member member) {
        // Insert the member if they doesn't already exist in the database
        List<Member> results = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class)
                .setParameter("login", member.getM_login())
                .getResultList();

        if (results.isEmpty()) {
            em.persist(member);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean registerContact(Contact contact) {
        // Insert the contact if it doesn't already exist in the database
        List<Contact> results = em.createQuery("SELECT c FROM Contact c WHERE c.c_login = :login", Contact.class)
                .setParameter("login", contact.getC_login())
                .getResultList();

        if (results.isEmpty()) {
            em.persist(contact);
            return true;
        }
        return false;
    }

    // OFFERS

    public List<Offer> getOffersByCategory(String category) {
        // Get all the offers by category, category can be null
        if  (category == null) {
            return em.createQuery("SELECT o FROM Offer o WHERE o.of_status = 'ACTIVE' ORDER BY o.of_date",  Offer.class).getResultList();
        }

        // Verify if the category exists
        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM Category c WHERE c.c_name = :category",
                        Long.class)
                .setParameter("category", category)
                .getSingleResult();

        if (count == 0) {
            throw new IllegalArgumentException("Category '" + category + "' doesn't exist");
        }

        return em.createQuery(
                        "SELECT o FROM Offer o " +
                                "JOIN o.of_categories c " +
                                "WHERE c.c_name = :category " +
                                "AND o.of_status = 'ACTIVE' " +
                                "ORDER BY o.of_date",
                        Offer.class
                )
                .setParameter("category", category)
                .getResultList();
    }


    private List<Offer> getOffersByName(String keyword){
        // Get all the offers that have the keyword in their name
        return em.createQuery(
                        "SELECT o FROM Offer o " +
                                "WHERE LOWER(o.ofName) LIKE LOWER(:keyword)",
                        Offer.class
                )
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    private boolean postOffer(Set<Category> offerCategories, String offerName, String description){
        // Allowed a Contact to publish an Offer if the name is different than any others

        // First, we verify than there is no other offer with the same name
        Long count = em.createQuery(
                        "SELECT COUNT(o) FROM Offer o WHERE LOWER(o.of_name) = LOWER(:name)",
                        Long.class
                )
                .setParameter("name", offerName)
                .getSingleResult();

        if (count > 0) {
            return false;
        }

        // Create the offer
        Offer offer = new Offer();
        offer.setOf_name(offerName);
        offer.setOf_categories(offerCategories);
        offer.setOf_description(description);
        offer.setOf_date(LocalDateTime.now());
        offer.setOf_status(Status.ACTIVE);

        em.persist(offer);
        return true;

    }

    public int nbOffersByOrganizations(Organization organization){
        // Get the offers' number published by the organization
        return 1;
    }

    public int nbOffersWinsByOrganization(Category category){
        // Get the number of offers wins by the Organization
        return 1;
    }


    public boolean checkLP(String login, String password) {
        Query q = em.createQuery("SELECT m From Member m where m.m_login='"+login+"'");
        Member m = null;
        try{
            m = (Member)q.getSingleResult();
        } catch(Exception e){
            return false;
        }
        if(m != null){
            return m.getM_password().equals(password);
        }
        else {
            return false;
        }
    }

    public Integer retrieveMemberId(String login) {
        Query q = em.createQuery("SELECT m.m_id From Member m where m.m_login='"+login+"'", Integer.class);
        Integer mem_id = null;
        try {
            mem_id = (Integer)q.getSingleResult();
        } catch(Exception e){
            return null;
        }
        return mem_id;
    }



    public boolean alreadySubmitted(int member_id, int offer_id) {
        Query q = em.createQuery("SELECT w FROM Wish w WHERE w.w_member.m_id=:m_id AND w.w_offer.of_id=:o_id AND w.w_status=:stat", Wish.class);
        q.setParameter("m_id", member_id);
        q.setParameter("o_id", offer_id);
        q.setParameter("stat", DELIVERED);
        try {
            return (Wish)q.getSingleResult() != null;
        } catch (NoResultException e){
            return false;
        }
    }

//    @Transactional
//    public boolean wish(int mem_id, int of_id){
//        if(!isMemberInOrganisation(mem_id, of_id)) {//
//            Member m = em.find(Member.class,mem_id);
//            Offer o = em.find(Offer.class,of_id);
//            Wish d = new Wish(
//                    LocalDateTime.now(),
//                    DELIVERED,
//                    o,
//                    m
//            );
//            em.persist(d);
//            return true;
//        } else{
//            return false;
//        }
//    }

//    public boolean isMemberInOrganisation(int mem_id, int of_id){        // Check if a member see the offer in the same org
//        Member m = em.find(Member.class,mem_id);
//        Offer o = em.find(Offer.class,of_id);
//        return m.getM_organisation().getO_id() == o.getOf_contact().getR_organisation().getO_id();
//    }

    public List<Wish> getWishesByMember(int mem_id){
        Member m = em.find(Member.class,mem_id);
        List<Wish> wishes = m.getM_wishes();
        return wishes;
    }

    public List<Wish> getWishesByOffer(int offer_id){
        Offer of = em.find(Offer.class,offer_id);
        List<Wish> wishes = of.getOf_wishes();
        return wishes;
    }






}
