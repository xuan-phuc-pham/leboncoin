package entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import type.WishStatus;
@Entity
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int w_id;

    private LocalDateTime w_date;

    @Enumerated(EnumType.STRING)
    private WishStatus w_status;

    @ManyToOne
    private Offer w_offer;

    @ManyToOne
    private Member w_member;

    public Wish() {
    }

    public Wish(LocalDateTime w_date, WishStatus w_status, Offer w_offer, Member w_member) {
        this.w_date = w_date;
        this.w_status = w_status;
        this.w_offer = w_offer;
        this.w_member = w_member;
    }

    public int getW_id() {
        return w_id;
    }

    public LocalDateTime getW__date() {
        return w_date;
    }

    public void setW_date(LocalDateTime w_date) {
        this.w_date = w_date;
    }

    public WishStatus getW_status() {
        return w_status;
    }

    public void setW_status(WishStatus w_status) {
        this.w_status = w_status;
    }

    public Offer getW_offer() {
        return w_offer;
    }

    public void setW_offer(Offer w_offer) {
        this.w_offer = w_offer;
    }

    public Member getW_member() {
        return w_member;
    }

    public void setW_member(Member w_member) {
        this.w_member = w_member;
    }



}


