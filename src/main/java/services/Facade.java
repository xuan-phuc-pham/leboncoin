package services;

import entities.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static type.WishStatus.DELIVERED;

@Service
public class Facade {
    @PersistenceContext
    private EntityManager em;


    public Facade() {
    }

    public List<Offer> getOffers(){
        List<Offer> offers = em.createQuery("SELECT o FROM Offer o WHERE o.of_status ='ACTIVE' ORDER BY o.of_date", Offer.class).getResultList();
        return offers;
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
        return m.getM_organisation().getO_id() == o.getOf_contact().getR_organisation().getO_id();
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
