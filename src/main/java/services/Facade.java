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

    // ------------------------------
    // CHECK AND REGISTER USERS
    // ------------------------------

    public Member checkMember(String login, String password) {
        // Search for a member by login and verify the password
        try {
            Query q = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class);
            q.setParameter("login", login);

            Member member = (Member) q.getSingleResult();

            // Return the member only if the password matches
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
        // Search for a contact by login and verify the password
        try {
            Query q = em.createQuery("SELECT c FROM Contact c WHERE c.c_login = :login", Contact.class);
            q.setParameter("login", login);

            Contact contact = (Contact) q.getSingleResult();

            // Return the contact only if the password matches
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
        // Register a new member if the login does not already exist
        List<Member> results = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class)
                .setParameter("login", login)
                .getResultList();

        if (results.isEmpty()) {
            Member member = new Member(login, password, firstName, lastName, organization);
            em.persist(member);
            return true;
        }

        // Return false if the member already exists
        return false;
    }

    @Transactional
    public Contact registerContact(String login, String password, String firstName, String lastName, String organizationName, String organizationDescription) {
        // Register a new contact if the login does not already exist
        List<Contact> results = em.createQuery("SELECT c FROM Contact c WHERE c.c_login = :login", Contact.class)
                .setParameter("login", login)
                .getResultList();

        if (results.isEmpty()) {
            Contact contact = new Contact(login, password, firstName, lastName, organizationName, organizationDescription);
            em.persist(contact);
            em.persist(contact.getC_organization()); // Persist the associated organization
            return contact;
        }

        // Return null if the contact already exists
        return null;
    }

    // ------------------------------
    // DELETE USERS
    // ------------------------------
    @Transactional
    public void deleteMember(String login){
        // Find member(s) by login
        List<Member> members = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class)
                .setParameter("login", login)
                .getResultList();

        // If member exists, remove it (should be at most 1 if login is unique)
        if (!members.isEmpty()) {
            em.remove(members.getFirst());
        }
    }

    @Transactional
    public void deleteContact(String login){
        // Find contact(s) by login
        List<Contact> contacts = em.createQuery(
                        "SELECT c FROM Contact c WHERE c.c_login = :login",
                        Contact.class)
                .setParameter("login", login)
                .getResultList();

        // Remove the contact if it exists (login should be unique)
        if (!contacts.isEmpty()) {
            em.remove(contacts.getFirst());
        }
    }


    // ------------------------------
    // CATEGORY MANAGEMENT
    // ------------------------------

    public Category createCategory(String c_name) {
        // Create a new category if it doesn't already exist
        List<Category> res = em.createQuery(
                        "SELECT c FROM Category c WHERE c.c_name = :c_name",
                        Category.class
                )
                .setParameter("c_name", c_name)
                .getResultList();

        if (res.isEmpty()) {
            Category c = new Category(c_name);
            em.persist(c);
            return c;
        } else {
            // Return null if the category already exists
            return null;
        }
    }

    public Category getCategoryByName(String c_name) {
        // Retrieve a category by name or return null if not found
        try {
            return em.createQuery(
                            "SELECT c FROM Category c WHERE c.c_name = :c_name",
                            Category.class
                    )
                    .setParameter("c_name", c_name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // ------------------------------
    // OFFERS
    // ------------------------------

    public List<Offer> getOffersByCategory(String category) {
        // Get all active offers for the given category
        // If category is null, return all active offers
        if  (category == null) {
            return em.createQuery("SELECT o FROM Offer o WHERE o.of_status = 'ACTIVE' ORDER BY o.of_date",  Offer.class).getResultList();
        }

        // Check if the category exists
        Long count = em.createQuery(
                        "SELECT COUNT(c) FROM Category c WHERE c.c_name = :category",
                        Long.class)
                .setParameter("category", category)
                .getSingleResult();

        if (count == 0) {
            throw new IllegalArgumentException("Category '" + category + "' doesn't exist");
        }

        // Return all active offers for the given category ordered by date
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

    public List<Offer> getOffersByName(String keyword){
        // Get all offers that contain the keyword in their name (case-insensitive)
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        return em.createQuery(
                        "SELECT o FROM Offer o " +
                                "WHERE LOWER(o.of_name) LIKE LOWER(:keyword)",
                        Offer.class
                )
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

    public boolean postOffer(Contact contact, Set<Category> categories, String name, String description){
        // Allow a contact to post a new offer if no offer with the same name exists

        // Check for duplicate offer names
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

    public boolean validateOffer(Offer offer){
        // Accept the first wish and reject the others for a given offer
        List<Wish> wishes = getWishesByOffer(offer.getOf_id()); // must return the wishes in order
        if(wishes != null){
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
            // No wishes found for this offer
            return false;
        }
    }

    // ------------------------------
    // WISH MANAGEMENT
    // ------------------------------

    public List<Wish> getWishesByMember(int member_id){
        // Get all pending wishes for a given member
        TypedQuery<Wish> q = em.createQuery(
                "SELECT w FROM Wish w WHERE w.w_member.m_id=:m_id AND w.w_status=:stat",
                Wish.class
        );
        q.setParameter("m_id", member_id);
        q.setParameter("stat", WishStatus.AWAITING);

        return q.getResultList();
    }

    public List<Wish> getWishesByOffer(int offer_id){
        // Get all pending wishes for a given offer
        TypedQuery<Wish> q = em.createQuery(
                "SELECT w FROM Wish w WHERE w.w_offer.of_id=:of_id AND w.w_status=:stat",
                Wish.class
        );
        q.setParameter("of_id", offer_id);
        q.setParameter("stat", WishStatus.AWAITING);

        return q.getResultList();
    }

    public Wish findWish(Member member, Offer offer){
        // Find a pending wish for a member and a specific offer
        Query q = em.createQuery(
                "SELECT w FROM Wish w WHERE w.w_member.m_id=:m_id AND w.w_offer.of_id=:o_id AND w.w_offer.of_status=:o_status",
                Wish.class
        );
        q.setParameter("m_id", member.getM_id());
        q.setParameter("o_id", offer.getOf_id());
        q.setParameter("o_status", WishStatus.AWAITING);
        return (Wish) q.getSingleResult();
    }

    public boolean createWish(Member member, Offer offer){
        // Create a new wish for a member on a given offer if it doesn't already exist
        boolean wish_exists = findWish(member, offer) != null;
        if(wish_exists){
            return false;
        }
        else {
            Wish wish = new Wish(offer, member);
            em.persist(wish);
            return true;
        }
    }

    public boolean cancelWish(Member member, Offer offer){
        // Cancel a pending wish for a member on a given offer
        Wish wish = findWish(member, offer);
        if(wish == null){
            return false;
        }
        else {
            wish.setW_status(WishStatus.CANCELED);
            return true;
        }
    }

    public int getWishRank(Member member, Offer offer){
        // Get the rank/order of a member's wish among all pending wishes for the offer
        Wish wish = findWish(member, offer);
        if(wish != null){
            TypedQuery<Wish> q = em.createQuery(
                    "SELECT w FROM Wish w WHERE w.w_offer.of_id=:o_id AND w.w_offer.of_status=:o_status ORDER BY w.w_date",
                    Wish.class
            );
            q.setParameter("o_id", offer.getOf_id());
            q.setParameter("o_status", WishStatus.AWAITING);
            List<Wish> wishes = q.getResultList();
            for(int i = 0; i < wishes.size(); i++){
                if(wishes.get(i).getW_id() == wish.getW_id()){
                    return i;
                }
            }
        }
        // Member has no pending wish for this offer
        return -1;
    }
}
