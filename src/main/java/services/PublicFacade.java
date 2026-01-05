package services;

import dtos.MemberInfo;
import dtos.OfferInfo;
import entities.*;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PublicFacade {


    @PersistenceContext
    private EntityManager em;

    public List<Offer> getOffers(){
        List<Offer> offers = em.createQuery("SELECT o FROM Offer o WHERE o.of_status ='ACTIVE' ORDER BY o.of_date", Offer.class).getResultList();
        return offers;
    }

    public List<OfferInfo> getOffersInfo(List<Offer> lo){
        List<OfferInfo> list_offers = lo.stream()
                .map(o -> new OfferInfo(
                        o.getOf_id(),
                        o.getOf_contact().getR_organisation().getO_id(),
                        o.getOf_contact().getR_organisation().getO_name(),
                        o.getOf_contact().getR_fname()+" "+o.getOf_contact().getR_lname(),
                        o.getOf_description(),
                        o.getOf_name(),
                        o.getOf_date().toString(),
                        o.getStatus().toString()
                )).toList();
        return list_offers;
    }

    public OfferInfo getOfferById(int id){
        Offer o = em.find(Offer.class, id);
        return new OfferInfo(
                o.getOf_id(),
                o.getOf_contact().getR_organisation().getO_id(),
                o.getOf_contact().getR_organisation().getO_name(),
                o.getOf_contact().getR_fname()+" "+o.getOf_contact().getR_lname(),
                o.getOf_description(),
                o.getOf_name(),
                o.getOf_date().toString(),
                o.getStatus().toString()
        );
    }



}
