package services;

import dtos.DemandInfo;
import dtos.MemberInfo;
import dtos.OfferInfo;
import entities.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.*;

import static type.DemandStatus.DELIVERED;

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
        Query q = em.createQuery("SELECT de FROM Demande de WHERE de.de_member.m_id=:m_id AND de.de_offer.of_id=:o_id AND de.de_status=:stat", Demande.class);
        q.setParameter("m_id", member_id);
        q.setParameter("o_id", offer_id);
        q.setParameter("stat", DELIVERED);
        try {
            return (Demande)q.getSingleResult() != null;
        } catch (NoResultException e){
            return false;
        }
    }

    public MemberInfo getMemberInfo(Integer mem_id){
        Member m = em.find(Member.class,mem_id);
        Organisation o = m.getM_organisation();
        MemberInfo mi = new MemberInfo(
                m.getM_fname(),
                m.getM_lname(),
                m.getM_login(),
                o.getO_name(),
                o.getO_id()
        );
        return mi;
    }

    public DemandInfo getDemandInfo(int demand_id){
        Demande de = em.find(Demande.class,demand_id);
        Member m = de.getDe_member();
        Organisation o = m.getM_organisation();
        Offer of = de.getDe_offer();
        return new DemandInfo(
                demand_id,
                m.getM_id(),
                of.getOf_id(),
                m.getM_fname()+" "+m.getM_lname(),
                o.getO_name(),
                de.getDe_status().toString()
        );
    }

    public List<DemandInfo> getDemandsInfo(List<Demande> demands){
        List<DemandInfo> list_demande = demands.stream()
                .map(de -> new DemandInfo(
                        de.getDe_id(),
                        de.getDe_member().getM_id(),
                        de.getDe_offer().getOf_id(),
                        de.getDe_member().getM_fname()+" "+ de.getDe_member().getM_lname(),
                        de.getDe_member().getM_organisation().getO_name(),
                        de.getDe_status().toString()
                )).toList();
        return list_demande;
    }

    @Transactional
    public boolean demande(int mem_id, int of_id){
        if(!isMemberInOrganisation(mem_id, of_id)) {//
            Member m = em.find(Member.class,mem_id);
            Offer o = em.find(Offer.class,of_id);
            Demande d = new Demande(
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
        return m.getM_organisation().getO_id() == o.getOf_responsable().getR_organisation().getO_id();
    }

    public List<Demande> getDemandesByMember(int mem_id){
        Member m = em.find(Member.class,mem_id);
        List<Demande> demandes = m.getM_demandes();
        return demandes;
    }

    public List<Demande> getDemandesByOffer(int offer_id){
        Offer of = em.find(Offer.class,offer_id);
        List<Demande> demandes = of.getOf_demandes();
        return demandes;
    }






}
