package services;

import dtos.NewOffer;
import dtos.RegMember;
import dtos.RegOrgContact;
import entities.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import type.Status;
import type.WishStatus;

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



//    @Transactional
//    public boolean registerMember(Member member) {
//        // Insert the member if they doesn't already exist in the database
//        List<Member> results = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class)
//                .setParameter("login", member.getM_login())
//                .getResultList();
//
//        if (results.isEmpty()) {
//            em.persist(member);
//            return true;
//        }
//
//        return false;
//    }

    @Transactional
    public boolean registerMember(RegMember member) {
        List<Member> results = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class)
                .setParameter("login", member.login())
                .getResultList();
        Organization organization = em.find(Organization.class, member.org_id());
        if (!results.isEmpty() || organization == null) {
            return false;
        }else {
            Member mem = new Member(member.login(), member.password(), member.fname(), member.lname());
            mem.setM_organisation(organization);
            em.persist(mem);
            return true;
        }
    }

    public boolean checkMemberByLogin (String login){
        List<Member> results = em.createQuery("SELECT m FROM Member m WHERE m.m_login = :login", Member.class)
                .setParameter("login", login)
                .getResultList();
        return !results.isEmpty();
    }

    public boolean checkContactByLogin (String login){
        List<Contact> results = em.createQuery("SELECT m FROM Contact m WHERE m.c_login = :login", Contact.class)
                .setParameter("login", login)
                .getResultList();
        return !results.isEmpty();
    }



    @Transactional
    public boolean registerContact(RegOrgContact orgContact) {
        List<Contact> results = em.createQuery("SELECT c FROM Contact c WHERE c.c_login = :login", Contact.class)
                .setParameter("login", orgContact.contact_login())
                .getResultList();

        if (!results.isEmpty()) {
            return false;
        }else {
            Organization org = new Organization(orgContact.org_name(), orgContact.org_description());
            Contact con = new Contact(
                    orgContact.contact_login(),
                    orgContact.contact_pword(),
                    orgContact.contact_fname(),
                    orgContact.contact_lname(),
                    org
                    );
            em.persist(org);
            em.persist(con);
            return true;
        }
    }

    // OFFERS

    public List<Offer> getMyOffers(int org_id) {
        Organization org = em.find(Organization.class, org_id);
        return org.getContact().getC_offers();
    }


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

//    private boolean validateOffer(int offerId){
//
//    }


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

    @Transactional
    public boolean postOffer(NewOffer offer){

        Long count = em.createQuery(
                        "SELECT COUNT(o) FROM Offer o WHERE LOWER(o.of_name) = LOWER(:name)",
                        Long.class
                )
                .setParameter("name", offer.name())
                .getSingleResult();
        if (count > 0) {
            return false;
        }

        Contact contact = em.find(Contact.class, offer.rep_id());
        Offer new_offer = new Offer(contact, Status.ACTIVE, LocalDateTime.now(), offer.name(), offer.description());
        em.persist(new_offer);
        List<Category> categories = em.createQuery("SELECT c FROM Category c WHERE c.c_id IN :ids", Category.class)
                        .setParameter("ids", offer.catagories_ids()).getResultList();
        new_offer.setOf_categories(categories);
        categories.forEach( category -> {
            List<Offer> new_o_list = category.getOffers();
            new_o_list.add(new_offer);
            category.setOffers(new_o_list);
        });
        return true;
    }

    public boolean checkOfferExistsByName(String keyword){
        return !getOffersByName(keyword).isEmpty();
    }

    @Transactional
    public boolean cancel_offer( int offer_id ){
        Offer offer = em.find(Offer.class, offer_id);
        if(offer == null){
            return false;
        } else if (isNotActive(offer_id)){
            return false;
        }
        offer.setStatus(Status.CANCELLED);
        List<Wish> wishes = offer.getOf_wishes();
        wishes.forEach(wish -> {
            wish.setW_status(WishStatus.REJECTED);
        });
        return true;
    }

    @Transactional
    public boolean validate_offer( int offer_id ){
        Offer offer = em.find(Offer.class, offer_id);
        if(offer == null){
            return false;
        } else if(offer.getStatus() != Status.ACTIVE){
            return false;
        } else if (offer.getOf_wishes().isEmpty()) {
            return false;
        } else {
            Wish soonest_wish = em.createQuery("SELECT w FROM Wish w WHERE w.w_offer = :of ORDER BY w.w_date ASC LIMIT 1", Wish.class)
                    .setParameter("of", offer).getSingleResult();

            List<Wish> wishes = offer.getOf_wishes();
            wishes.forEach(w -> {
                w.setW_status(WishStatus.REJECTED);
            });
            soonest_wish.setW_status(WishStatus.ACCEPTED);
            offer.setStatus(Status.ACCEPTED);
            return true;
        }
    }

    //Test
    public boolean checkOfferExistById(int offer_id){
        Offer offer = em.find(Offer.class, offer_id);
        return offer != null;
    }
    //Test
    public boolean isNotActive(int offer_id){
        Offer offer = em.find(Offer.class, offer_id);
        return offer.getStatus() != Status.ACTIVE;
    }
    //Test
    public boolean hasNoWish(int offer_id){
        Offer offer = em.find(Offer.class, offer_id);
        return offer.getOf_wishes().isEmpty();
    }

    public Long nbOffersByOrganizations(int organization_id){
        Long count = em.createQuery("SELECT COUNT(o) FROM Offer o WHERE o.of_contact.c_organization.id = :organization_id AND o.of_status = :status", Long.class)
                .setParameter("organization_id", organization_id)
                .setParameter("status", Status.ACCEPTED)
                .getSingleResult();
        return count;
    }

    public Long nbOffersWonByOrganizations(int organization_id){
        // Nombre de demandes accepte par tous les membre d'asso
        Long count = em.createQuery("SELECT COUNT(w) FROM Wish w WHERE w.w_status = :status  AND w.w_member.m_organization.id = :org ", Long.class)
                .setParameter("org", organization_id)
                .setParameter("status", WishStatus.ACCEPTED)
                .getSingleResult();
        return count;
    }

    // WISHES

    public List<Wish> getMyWishes(int member_id){
        List<Wish> wishes = em.find(Member.class,member_id).getM_wishes();
        return wishes;
    }

    public Long getRank(int wish_id ){
        Offer offer = em.find(Wish.class, wish_id).getW_offer();

        String jpql = "SELECT COUNT(w) + 1 FROM Wish w " +
                "WHERE w.w_offer = :offer " +
                "AND w.w_date < (SELECT w2.w_date FROM Wish w2 WHERE w2.id = :wish_id)";

        Long rank = em.createQuery(jpql, Long.class)
                .setParameter("offer", offer)
                .setParameter("wish_id", wish_id)
                .getSingleResult();
        return rank;
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

    @Transactional
    public boolean wish(int mem_id, int of_id){
        if(!isMemberInOrganisation(mem_id, of_id)) {//
            Member m = em.find(Member.class,mem_id);
            Offer o = em.find(Offer.class,of_id);
            Wish d = new Wish(
                    LocalDateTime.now(),
                    DELIVERED,
                    o,
                    m
            );
            em.persist(d);
            return true;
        } else{
            return false;
        }
    }

    public boolean isMemberInOrganisation(int mem_id, int of_id){        // Check if a member see the offer in the same org
        Member m = em.find(Member.class,mem_id);
        Offer o = em.find(Offer.class,of_id);
        return m.getM_organisation().getO_id() == o.getOf_contact().getC_organization().getO_id();
    }

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
