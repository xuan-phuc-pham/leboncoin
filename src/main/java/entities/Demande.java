package entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import type.Status;
@Entity
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long de_id;

    private LocalDateTime de_date;

    private Status de_status;

    @ManyToOne
    private Offer de_offer;

    @ManyToOne
    private Member de_member;

    public Demande() {
    }

    public Demande(LocalDateTime de_date, Status de_status, Offer de_offer, Member de_member) {
        this.de_date = de_date;
        this.de_status = de_status;
        this.de_offer = de_offer;
        this.de_member = de_member;
    }

    public Long getDe_id() {
        return de_id;
    }

    public LocalDateTime getDe_date() {
        return de_date;
    }

    public void setDe_date(LocalDateTime de_date) {
        this.de_date = de_date;
    }

    public Status getDe_status() {
        return de_status;
    }

    public void setDe_status(Status de_status) {
        this.de_status = de_status;
    }

    public Offer getDe_offer() {
        return de_offer;
    }

    public void setDe_offer(Offer de_offer) {
        this.de_offer = de_offer;
    }

    public Member getDe_member() {
        return de_member;
    }

    public void setDe_member(Member de_member) {
        this.de_member = de_member;
    }
}
