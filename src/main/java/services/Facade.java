package services;

import entities.*;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import type.Status;
import type.WishStatus;

import java.util.*;

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
    public boolean registerMember(String login, String password, String firstName, String lastName, Organization organization) {
        // Insert the member if they don't already exist in the database
        List<Member> results = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class)
                .setParameter("login", login)
                .getResultList();

        if (results.isEmpty()) {
            Member member = new Member(login, password, firstName, lastName, organization);
            em.persist(member);
            return true;
        }

        return false;
    }

    @Transactional
    public Contact registerContact(String login, String password, String firstName, String lastName, String organizationName, String organizationDescription) {
        // Insert the contact if it doesn't already exist in the database
        List<Contact> results = em.createQuery("SELECT c FROM Contact c WHERE c.c_login = :login", Contact.class)
                .setParameter("login", login)
                .getResultList();

        if (results.isEmpty()) {
            Contact contact = new Contact(login, password, firstName, lastName, organizationName, organizationDescription);
            em.persist(contact);
            em.persist(contact.getC_organization());
            return contact;
        }
        return null;
    }

    public void deleteMember(String login){
        Query q = em.createQuery("SELECT m From Member m where m.m_login= :login",Member.class);
        q.setParameter("login",login);
        Member member = (Member) q.getSingleResult();

        if(member != null){
            em.remove(member);
        }
    }

    public void deleteContact(String login){
        Query q = em.createQuery("SELECT c From Contact c where c.c_login= :login",Contact.class);
        q.setParameter("login",login);
        Contact contact = (Contact) q.getSingleResult();

        if(contact != null){
            em.remove(contact);
        }
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
                                "WHERE LOWER(o.of_name) LIKE LOWER(:keyword)",
                        Offer.class
                )
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    private boolean postOffer(Contact contact, Set<Category> categories, String name, String description){
        // Allowed a Contact to publish an Offer if the name is different than any others

        // First, we verify than there is no other offer with the same name
        Long count = em.createQuery(
                        "SELECT COUNT(o) FROM Offer o WHERE LOWER(o.of_name) = LOWER(:name)",
                        Long.class
                )
                .setParameter("name", name)
                .getSingleResult();

        if (count > 0) {
            return false;
        }

        Offer offer = new Offer(contact, name, description, categories);
        em.persist(offer);
        return true;

    }

    //Return false if there are no wishes to accept
    public boolean validateOffer(Offer offer){
        List<Wish> wishes = getWishesByOffer(offer.getOf_id()); //doit renvoyer les éléments dans l'ordre
        if(wishes !=null){
            Wish accepted_wish = wishes.getFirst();
            accepted_wish.setW_status(WishStatus.ACCEPTED);
            wishes.remove(accepted_wish);
            for(Wish wish : wishes){
                wish.setW_status(WishStatus.REJECTED);
            }
            offer.setOf_status(Status.ARCHIVED);
            return true;
        }
        else{
            return false;
        }
    }

    public int nbOffersByOrganizations(Organization organization){
        // Get the offers' number published by the organization
        return 1;
    }

    public int nbOffersWinsByOrganization(Category category){
        // Get the number of offers wins by the Organization
        return 1;
    }


    public boolean checkLoginPassword(String login, String password) {
        Query q = em.createQuery("SELECT m From Member m where m.m_login= :login");
        q.setParameter("login",login);
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

    public List<Wish> getWishesByMember(int member_id){
        TypedQuery<Wish> q = em.createQuery("SELECT w FROM Wish w WHERE w.w_member.m_id=:m_id AND w.w_status=:stat", Wish.class);
        q.setParameter("m_id", member_id);
        q.setParameter("stat", WishStatus.AWAITING);

        return q.getResultList();
    }

    public List<Wish> getWishesByOffer(int offer_id){
        TypedQuery<Wish> q = em.createQuery("SELECT w FROM Wish w WHERE w.w_offer.of_id=:of_id AND w.w_status=:stat", Wish.class);
        q.setParameter("of_id", offer_id);
        q.setParameter("stat", WishStatus.AWAITING);

        return q.getResultList();
    }

    //Functions for wishes

    //Adds a wish to an offer
    //Return false if the wish can't be created, true otherwise
    public Wish findWish(Member member, Offer offer){
        Query q = em.createQuery("SELECT w FROM Wish w WHERE w.w_member.m_id=:m_id AND w.w_offer.of_id=:o_id AND w.w_offer.of_status=:o_status", Wish.class);
        q.setParameter("m_id", member.getM_id());
        q.setParameter("o_id", offer.getOf_id());
        q.setParameter("o_status", WishStatus.AWAITING);
        return (Wish)q.getSingleResult();
    }

    public boolean createWish(Member member, Offer offer){
        boolean wish_exists = findWish(member, offer) != null;
        if(wish_exists){
            //This member already made a wish for this offer
            return false;
        }
        else {
            Wish wish = new Wish(offer, member);
            em.persist(wish);
            return true;
        }
    }

    public boolean cancelWish(Member member, Offer offer){
        Wish wish = findWish(member, offer);
        if(wish == null){
            //This member does not have an awaiting wish for this offer
            return false;
        }
        else {
            //The wish status changes to CANCELED
            wish.setW_status(WishStatus.CANCELED);
            return true;
        }
    }

    public int getWishRank(Member member, Offer offer){
        Wish wish = findWish(member, offer);
        if(wish != null){
            TypedQuery<Wish> q = em.createQuery("SELECT w FROM Wish w WHERE w.w_offer.of_id=:o_id AND w.w_offer.of_status=:o_status ORDER BY w.w_date", Wish.class);
            q.setParameter("o_id", offer.getOf_id());
            q.setParameter("o_status", WishStatus.AWAITING);
            List<Wish> wishes = q.getResultList();
            for(int i = 0; i<wishes.size();i++){
                if(wishes.get(i).getW_id() == wish.getW_id()){
                    return i;
                }
            }
        }
        //This member does not have an awaiting wish for this offer
        return -1;
    }



}
