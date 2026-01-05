package services;

import dtos.WishInfo;
import dtos.MemberInfo;
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
public class MemFacade {
    @PersistenceContext
    private EntityManager em;

    private final PublicFacade publicFacade;

    public MemFacade(PublicFacade publicFacade) {
        this.publicFacade = publicFacade;
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

    public PublicFacade getPublicFacade() {
        return publicFacade;
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

    public MemberInfo getMemberInfo(Integer mem_id){
        Member m = em.find(Member.class,mem_id);
        Organization o = m.getM_organisation();
        MemberInfo mi = new MemberInfo(
                m.getM_firstName(),
                m.getM_lastName(),
                m.getM_login(),
                o.getO_name(),
                o.getO_id()
        );
        return mi;
    }

    public WishInfo getDemandInfo(int demand_id){
        Wish de = em.find(Wish.class,demand_id);
        Member m = de.getW_member();
        Organization o = m.getM_organisation();
        Offer of = de.getW_offer();
        return new WishInfo(
                demand_id,
                m.getM_id(),
                of.getOf_id(),
                m.getM_firstName()+" "+m.getM_lastName(),
                o.getO_name(),
                de.getW_status().toString()
        );
    }

    public List<WishInfo> getDemandsInfo(List<Wish> demands){
        List<WishInfo> list_wishes = demands.stream()
                .map(de -> new WishInfo(
                        de.getW_id(),
                        de.getW_member().getM_id(),
                        de.getW_offer().getOf_id(),
                        de.getW_member().getM_firstName()+" "+ de.getW_member().getM_lastName(),
                        de.getW_member().getM_organisation().getO_name(),
                        de.getW_status().toString()
                )).toList();
        return list_wishes;
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
