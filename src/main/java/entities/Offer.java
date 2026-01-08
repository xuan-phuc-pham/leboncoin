package entities;

import jakarta.persistence.*;

import type.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int of_id;

    @ManyToOne
    private Contact of_contact;

    @ManyToMany
    private List<Category> of_categories;

    @OneToMany(mappedBy = "w_offer", fetch = FetchType.EAGER)
    private List<Wish> of_wishes;

    @Enumerated(EnumType.STRING)
    private Status of_status;

    private LocalDateTime of_date;
    private String of_name;
    private String of_description;



    public Offer() {
    }

    public Offer(Contact of_contact, Status of_status, LocalDateTime of_date, String of_name, String of_description) {
        this.of_contact = of_contact;
        this.of_status = of_status;
        this.of_date = of_date;
        this.of_name = of_name;
        this.of_description = of_description;
    }

    public Status getStatus() {
        return of_status;
    }

    public LocalDateTime getOf_date() {
        return of_date;
    }

    public void setOf_date(LocalDateTime of_date) {
        this.of_date = of_date;
    }

    public Status getOf_status() {
        return of_status;
    }

    public void setOf_status(Status of_status) {
        this.of_status = of_status;
    }

    public void setStatus(Status status) {
        this.of_status = status;
    }

    public int getOf_id() {
        return of_id;
    }

    public Contact getOf_contact() {
        return of_contact;
    }

    public void setOf_contact(Contact of_contact) {
        this.of_contact = of_contact;
    }

    public List<Category> getOf_categories() {
        return of_categories;
    }

    public void setOf_categories(List<Category> of_categories) {
        this.of_categories = of_categories;
    }

    public List<Wish> getOf_wishes() {
        return of_wishes;
    }

    public void setOf_wishes(List<Wish> of_wishes) {
        this.of_wishes = of_wishes;
    }

    public String getOf_name() {
        return of_name;
    }

    public void setOf_name(String of_name) {
        this.of_name = of_name;
    }

    public String getOf_description() {
        return of_description;
    }

    public void setOf_description(String of_description) {
        this.of_description = of_description;
    }

}
