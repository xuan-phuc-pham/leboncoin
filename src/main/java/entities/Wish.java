package entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import type.DemandStatus;
@Entity
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int w_id;

    private LocalDateTime w_date;

    @Enumerated(EnumType.STRING)
    private DemandStatus w_status;

    @ManyToOne
    private Offer w_offer;

    @ManyToOne
    private Member w_member;

    public Wish() {
    }

    public Wish(LocalDateTime w_date, DemandStatus w_status, Offer w_offer, Member w_member) {
        this.w_date = w_date;
        this.w_status = w_status;
        this.w_offer = w_offer;
        this.w_member = w_member;
    }

    public int getDe_id() {
        return w_id;
    }

    public LocalDateTime getDe_date() {
        return w_date;
    }

    public void setDe_date(LocalDateTime w_date) {
        this.w_date = w_date;
    }

    public DemandStatus getDe_status() {
        return w_status;
    }

    public void setDe_status(DemandStatus w_status) {
        this.w_status = w_status;
    }

    public Offer getDe_offer() {
        return w_offer;
    }

    public void setDe_offer(Offer w_offer) {
        this.w_offer = w_offer;
    }

    public Member getDe_member() {
        return w_member;
    }

    public void setDe_member(Member w_member) {
        this.w_member = w_member;
    }
}
